package app.components;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import app.Clock;
import app.db.entity.Bar;
import app.db.entity.BarTable;
import app.db.entity.Reservation;
import app.db.repositories.BarRepository;
import app.db.repositories.BarTableRepository;
import app.db.repositories.ReservationRepository;
import app.dto.MessageResponse;
import app.dto.TableRequest;
import app.dto.TableResponse;
import app.rest.errorhandlers.ApiException;

/** Each bar's bookable tables, and which ones are free for a time slot. */
@Component
@Transactional
public class TableComponent {

	private static final int DAY_MINUTES = 24 * 60;

	@Autowired private BarRepository barRepository;
	@Autowired private BarTableRepository barTableRepository;
	@Autowired private ReservationRepository reservationRepository;

	@Transactional(readOnly = true)
	public List<TableResponse> getTablesForBar(Long barId) {
		loadBar(barId);
		List<TableResponse> out = new ArrayList<>();
		for (BarTable t : barTableRepository.findByBarIdOrderByLabelAsc(barId)) {
			out.add(TableResponse.from(t));
		}
		return out;
	}

	@Transactional(readOnly = true)
	public TableResponse getTable(Long id) {
		return TableResponse.from(load(id));
	}

	@Transactional(readOnly = true)
	public List<TableResponse> getAvailableTables(Long barId, LocalDate date, LocalTime start, LocalTime end, int guests) {
		Bar bar = loadBar(barId);
		validateSlot(bar, date, start, end);

		List<BarTable> free = new ArrayList<>();
		for (BarTable t : barTableRepository.findByBarIdAndActiveTrueAndCapacityGreaterThanEqual(barId, guests)) {
			if (findOverlap(t.getId(), date, start, end) == null) {
				free.add(t);
			}
		}
		free.sort(Comparator.comparing(BarTable::getCapacity).thenComparing(BarTable::getLabel));

		List<TableResponse> out = new ArrayList<>();
		for (BarTable t : free) {
			out.add(TableResponse.from(t));
		}
		return out;
	}

	public TableResponse createTable(TableRequest req) {
		if (req.barId == null) {
			throw ApiException.fieldError("barId", "must not be null");
		}
		Bar bar = loadBar(req.barId);
		BarComponent.checkOwner(bar, req.ownerId);
		String label = req.label.trim();
		if (barTableRepository.existsByBarIdAndLabelIgnoreCase(bar.getId(), label)) {
			throw ApiException.conflict("Bar " + bar.getId() + " already has a table labelled '" + label + "'.");
		}
		BarTable t = new BarTable();
		t.setBar(bar);
		t.setLabel(label);
		t.setTableType(req.tableType);
		t.setCapacity(req.capacity);
		t.setRate(req.rate);
		t.setActive(req.active == null ? Boolean.TRUE : req.active);
		return TableResponse.from(barTableRepository.save(t));
	}

	public TableResponse updateTable(Long id, TableRequest req) {
		BarTable t = load(id);
		BarComponent.checkOwner(t.getBar(), req.ownerId);
		String label = req.label.trim();
		if (!label.equalsIgnoreCase(t.getLabel())
				&& barTableRepository.existsByBarIdAndLabelIgnoreCase(t.getBar().getId(), label)) {
			throw ApiException.conflict("Bar " + t.getBar().getId() + " already has a table labelled '" + label + "'.");
		}
		t.setLabel(label);
		t.setTableType(req.tableType);
		t.setCapacity(req.capacity);
		t.setRate(req.rate);
		if (req.active != null) t.setActive(req.active);
		return TableResponse.from(barTableRepository.save(t));
	}

	public MessageResponse deleteTable(Long id, Long ownerId) {
		BarTable t = load(id);
		BarComponent.checkOwner(t.getBar(), ownerId);
		long upcoming = reservationRepository.countByTableIdAndReservationDateGreaterThanEqualAndStatusIn(id,
				Clock.today(), ReservationComponent.ACTIVE);
		if (upcoming > 0) {
			throw ApiException.conflict("Table " + id + " has "
					+ TimeFormat.plural(upcoming, "upcoming reservation", "upcoming reservations")
					+ ". Cancel them or set the table inactive instead.");
		}
		barTableRepository.delete(t);
		return new MessageResponse("Table " + id + " deleted.");
	}

	/**
	 * Checks a requested slot: the date is not in the past, end is after start (or past midnight when
	 * the bar closes after midnight), the slot is inside operating hours, and it has not already started.
	 * reservationDate is the calendar date the slot starts on.
	 */
	public void validateSlot(Bar bar, LocalDate date, LocalTime start, LocalTime end) {
		if (date.isBefore(Clock.today())) {
			throw ApiException.badRequest("Date " + date + " is in the past.");
		}
		LocalTime open = bar.getOpeningTime();
		LocalTime close = bar.getClosingTime();
		boolean barOpenPastMidnight = !close.isAfter(open);
		if (start.equals(end) || (end.isBefore(start) && !barOpenPastMidnight)) {
			throw ApiException.badRequest("endTime must be after startTime.");
		}

		int openMin = minutes(open);
		int openLength = mod(minutes(close) - openMin);
		if (openLength == 0) openLength = DAY_MINUTES; // open 24 hours
		int startOffset = mod(minutes(start) - openMin);
		int slotLength = mod(minutes(end) - minutes(start));
		if (startOffset + slotLength > openLength) {
			throw ApiException.badRequest("Requested time " + TimeFormat.hm(start) + "–" + TimeFormat.hm(end)
					+ " is outside the bar's operating hours (" + TimeFormat.hm(open) + "–" + TimeFormat.hm(close) + ").");
		}
		if (date.atTime(start).isBefore(Clock.now())) {
			throw ApiException.badRequest("Requested start time " + date + " " + TimeFormat.hm(start) + " has already passed.");
		}
	}

	/**
	 * Returns an active (PENDING/CONFIRMED) booking on this table that overlaps the slot, or null.
	 * Compares real date-times so slots that cross midnight are handled.
	 */
	public Reservation findOverlap(Long tableId, LocalDate date, LocalTime start, LocalTime end) {
		LocalDateTime newStart = date.atTime(start);
		LocalDateTime newEnd = newStart.plusMinutes(mod(minutes(end) - minutes(start)));
		for (Reservation r : reservationRepository.findByTableIdAndReservationDateBetweenAndStatusIn(tableId,
				date.minusDays(1), date.plusDays(1), ReservationComponent.ACTIVE)) {
			if (r.getStartDateTime().isBefore(newEnd) && newStart.isBefore(r.getEndDateTime())) {
				return r;
			}
		}
		return null;
	}

	BarTable load(Long id) {
		return barTableRepository.findById(id)
				.orElseThrow(() -> ApiException.notFound("Table with id " + id + " does not exist."));
	}

	private Bar loadBar(Long id) {
		return barRepository.findById(id)
				.orElseThrow(() -> ApiException.notFound("Bar with id " + id + " does not exist."));
	}

	private static int minutes(LocalTime t) {
		return t.getHour() * 60 + t.getMinute();
	}

	private static int mod(int m) {
		return ((m % DAY_MINUTES) + DAY_MINUTES) % DAY_MINUTES;
	}
}
