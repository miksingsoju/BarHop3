package app.components;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import app.Clock;
import app.db.entity.Bar;
import app.db.entity.BarTable;
import app.db.entity.Hopper;
import app.db.entity.Reservation;
import app.db.entity.ReservationStatus;
import app.db.repositories.BarRepository;
import app.db.repositories.BarTableRepository;
import app.db.repositories.HopperRepository;
import app.db.repositories.ReservationRepository;
import app.dto.MessageResponse;
import app.dto.ReservationRequest;
import app.dto.ReservationResponse;
import app.dto.StatusUpdateRequest;
import app.rest.errorhandlers.ApiException;

/** Table bookings: capacity, operating hours, no double booking, status changes, SMS triggers. */
@Component
@Transactional
public class ReservationComponent {

	private static final Logger log = LoggerFactory.getLogger(ReservationComponent.class);

	/** Statuses that hold a table. */
	public static final List<ReservationStatus> ACTIVE = Collections.unmodifiableList(
			Arrays.asList(ReservationStatus.PENDING, ReservationStatus.CONFIRMED));

	@Autowired private ReservationRepository reservationRepository;
	@Autowired private HopperRepository hopperRepository;
	@Autowired private BarTableRepository barTableRepository;
	@Autowired private BarRepository barRepository;
	@Autowired private TableComponent tableComponent;
	@Autowired private NotificationComponent notificationComponent;

	public ReservationResponse createReservation(ReservationRequest req) {
		Hopper hopper = hopperRepository.findById(req.hopperId)
				.orElseThrow(() -> ApiException.notFound("Hopper with id " + req.hopperId + " does not exist."));
		BarTable table = barTableRepository.findById(req.tableId)
				.orElseThrow(() -> ApiException.notFound("Table with id " + req.tableId + " does not exist."));
		Bar bar = table.getBar();

		if (!Boolean.TRUE.equals(req.agreedToPolicy)) {
			throw ApiException.badRequest("You must agree to the bar's house policies.");
		}
		if (!Boolean.TRUE.equals(table.getActive())) {
			throw ApiException.badRequest("'" + table.getLabel() + "' is not available for booking.");
		}
		if (req.guestCount > table.getCapacity()) {
			throw ApiException.badRequest("Guest count " + req.guestCount + " exceeds the capacity of '"
					+ table.getLabel() + "' (" + table.getCapacity() + ").");
		}
		tableComponent.validateSlot(bar, req.reservationDate, req.startTime, req.endTime);

		Reservation clash = tableComponent.findOverlap(table.getId(), req.reservationDate, req.startTime, req.endTime);
		if (clash != null) {
			throw ApiException.conflict("'" + table.getLabel() + "' is already booked from "
					+ TimeFormat.hm(clash.getStartTime()) + " to " + TimeFormat.hm(clash.getEndTime()) + " on "
					+ clash.getReservationDate() + ".");
		}

		Reservation r = new Reservation();
		r.setHopper(hopper);
		r.setTable(table);
		r.setReservationDate(req.reservationDate);
		r.setStartTime(req.startTime);
		r.setEndTime(req.endTime);
		r.setGuestCount(req.guestCount);
		r.setContactName(req.contactName.trim());
		r.setContactNumber(req.contactNumber);
		r.setAgreedToPolicy(Boolean.TRUE);
		r.setStatus(ReservationStatus.PENDING);
		r.setReminderSent(Boolean.FALSE);
		r.setCreatedAt(Clock.now());
		r = reservationRepository.save(r);

		notificationComponent.reservationPlaced(r);
		return ReservationResponse.from(r);
	}

	public ReservationResponse updateStatus(Long id, StatusUpdateRequest req) {
		Reservation r = load(id);
		checkOwner(r, req.ownerId);

		ReservationStatus target;
		try {
			target = ReservationStatus.valueOf(req.status.trim().toUpperCase());
		} catch (IllegalArgumentException e) {
			throw ApiException.badRequest("Invalid status '" + req.status
					+ "'. Allowed values: CONFIRMED, CANCELLED, COMPLETED, NO_SHOW.");
		}
		ReservationStatus current = r.getStatus();
		boolean allowed = (current == ReservationStatus.PENDING
				&& (target == ReservationStatus.CONFIRMED || target == ReservationStatus.CANCELLED))
				|| (current == ReservationStatus.CONFIRMED && (target == ReservationStatus.NO_SHOW
						|| target == ReservationStatus.COMPLETED || target == ReservationStatus.CANCELLED));
		if (!allowed) {
			throw ApiException.conflict("Cannot change a " + current + " reservation to " + target + ".");
		}

		r.setStatus(target);
		r = reservationRepository.save(r);
		if (target == ReservationStatus.CONFIRMED) {
			notificationComponent.reservationConfirmed(r);
		} else if (target == ReservationStatus.CANCELLED) {
			notificationComponent.reservationCancelledByBar(r);
		}
		return ReservationResponse.from(r);
	}

	public ReservationResponse cancelReservation(Long id, Long hopperId) {
		Reservation r = load(id);
		if (!r.getHopper().getId().equals(hopperId)) {
			throw ApiException.forbidden("Reservation " + id + " does not belong to hopper " + hopperId + ".");
		}
		if (!ACTIVE.contains(r.getStatus())) {
			throw ApiException.conflict("Reservation " + id + " is already " + r.getStatus() + " and cannot be cancelled.");
		}
		r.setStatus(ReservationStatus.CANCELLED);
		r = reservationRepository.save(r);
		notificationComponent.reservationCancelledByHopper(r);
		return ReservationResponse.from(r);
	}

	/** Cancels a booking because its bar or hopper is being deleted, texting the other side. */
	public void cancelBySystem(Reservation r, boolean notifyHopper, boolean notifyOwner) {
		r.setStatus(ReservationStatus.CANCELLED);
		reservationRepository.save(r);
		if (notifyHopper) notificationComponent.reservationCancelledByBar(r);
		if (notifyOwner) notificationComponent.reservationCancelledByHopper(r);
	}

	@Transactional(readOnly = true)
	public List<ReservationResponse> getReservations(Long hopperId, Long barId, LocalDate date) {
		List<Reservation> list;
		if (hopperId != null) {
			if (!hopperRepository.existsById(hopperId)) {
				throw ApiException.notFound("Hopper with id " + hopperId + " does not exist.");
			}
			list = reservationRepository.findByHopperIdOrderByReservationDateDescStartTimeDesc(hopperId);
		} else if (barId != null) {
			if (!barRepository.existsById(barId)) {
				throw ApiException.notFound("Bar with id " + barId + " does not exist.");
			}
			if (date != null) {
				list = reservationRepository.findByTableBarIdAndReservationDateOrderByStartTimeAsc(barId, date);
			} else {
				list = new ArrayList<>(reservationRepository
						.findByTableBarIdAndReservationDateGreaterThanEqualOrderByReservationDateAsc(barId, Clock.today()));
				list.sort((a, b) -> a.getStartDateTime().compareTo(b.getStartDateTime()));
			}
		} else {
			throw ApiException.badRequest("Provide either hopperId or barId.");
		}
		List<ReservationResponse> out = new ArrayList<>();
		for (Reservation r : list) {
			out.add(ReservationResponse.from(r));
		}
		return out;
	}

	@Transactional(readOnly = true)
	public ReservationResponse getReservation(Long id) {
		return ReservationResponse.from(load(id));
	}

	public MessageResponse deleteReservation(Long id, Long ownerId) {
		Reservation r = load(id);
		checkOwner(r, ownerId);
		if (ACTIVE.contains(r.getStatus())) {
			throw ApiException.conflict("Only CANCELLED, COMPLETED or NO_SHOW reservations can be deleted.");
		}
		reservationRepository.delete(r);
		return new MessageResponse("Reservation " + id + " deleted.");
	}

	/** Scheduler: texts a reminder for CONFIRMED bookings starting within the next 2 hours. */
	public int sendUpcomingReminders() {
		LocalDateTime now = Clock.now();
		LocalDateTime windowEnd = now.plusHours(2);
		List<LocalDate> dates = new ArrayList<>();
		dates.add(now.toLocalDate());
		if (!windowEnd.toLocalDate().equals(now.toLocalDate())) {
			dates.add(windowEnd.toLocalDate()); // early-morning slots after midnight
		}
		int sent = 0;
		for (LocalDate d : dates) {
			for (Reservation r : reservationRepository.findByStatusAndReminderSentFalseAndReservationDate(
					ReservationStatus.CONFIRMED, d)) {
				LocalDateTime start = r.getStartDateTime();
				if (!start.isBefore(now) && !start.isAfter(windowEnd)) {
					notificationComponent.reservationReminder(r);
					r.setReminderSent(Boolean.TRUE);
					reservationRepository.save(r);
					sent++;
				}
			}
		}
		return sent;
	}

	/** Scheduler: closes out past bookings. CONFIRMED -> COMPLETED, PENDING (never confirmed) -> CANCELLED. */
	public int[] closeOutPastReservations() {
		int completed = 0;
		int cancelled = 0;
		for (Reservation r : reservationRepository.findByReservationDateBeforeAndStatusIn(Clock.today(), ACTIVE)) {
			if (r.getStatus() == ReservationStatus.CONFIRMED) {
				r.setStatus(ReservationStatus.COMPLETED);
				completed++;
			} else {
				r.setStatus(ReservationStatus.CANCELLED);
				cancelled++;
			}
			reservationRepository.save(r);
		}
		log.info("Close-out: {} completed, {} cancelled", completed, cancelled);
		return new int[] { completed, cancelled };
	}

	private Reservation load(Long id) {
		return reservationRepository.findById(id)
				.orElseThrow(() -> ApiException.notFound("Reservation with id " + id + " does not exist."));
	}

	private static void checkOwner(Reservation r, Long ownerId) {
		if (!r.getBar().getOwner().getId().equals(ownerId)) {
			throw ApiException.forbidden("Owner " + ownerId + " does not own the bar for reservation " + r.getId() + ".");
		}
	}
}
