package app.components;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import app.Clock;
import app.db.entity.ActivityStatus;
import app.db.entity.Bar;
import app.db.entity.Owner;
import app.db.entity.Reservation;
import app.db.repositories.BarRepository;
import app.db.repositories.ReservationRepository;
import app.db.repositories.ReviewRepository;
import app.dto.BarDetail;
import app.dto.BarRequest;
import app.dto.BarSummary;
import app.dto.MessageResponse;
import app.rest.errorhandlers.ApiException;

/** Bar listings: search, details, owner-only create/update/delete, Metro Manila rule, ratings. */
@Component
@Transactional
public class BarComponent {

	@Autowired private BarRepository barRepository;
	@Autowired private ReviewRepository reviewRepository;
	@Autowired private ReservationRepository reservationRepository;
	@Autowired private OwnerComponent ownerComponent;
	@Autowired private ReservationComponent reservationComponent;

	@Transactional(readOnly = true)
	public List<BarSummary> searchBars(String city, String search, String status, String sort) {
		city = blankToNull(city);
		search = blankToNull(search);
		status = blankToNull(status);
		sort = blankToNull(sort);

		ActivityStatus statusFilter = null;
		if (status != null) {
			try {
				statusFilter = ActivityStatus.valueOf(status.toUpperCase());
			} catch (IllegalArgumentException e) {
				throw ApiException.badRequest("Invalid status '" + status + "'. Allowed values: QUIET, MODERATE, LIVELY, PACKED.");
			}
		}
		boolean byRating = false;
		if (sort != null) {
			if (sort.equalsIgnoreCase("rating")) {
				byRating = true;
			} else if (!sort.equalsIgnoreCase("likes")) {
				throw ApiException.badRequest("Invalid sort '" + sort + "'. Allowed values: likes, rating.");
			}
		}
		String cityFilter = city == null ? null : (MetroManila.canonical(city) != null ? MetroManila.canonical(city) : city);

		// Pick the most selective repository query, then apply the remaining filters in memory.
		List<Bar> bars;
		if (search != null) {
			bars = barRepository.findByNameContainingIgnoreCase(search);
		} else if (cityFilter != null) {
			bars = barRepository.findByCityIgnoreCase(cityFilter);
		} else if (statusFilter != null) {
			bars = barRepository.findByActivityStatus(statusFilter);
		} else {
			bars = barRepository.findAllByOrderByActiveLikeCountDesc();
		}

		List<Bar> filtered = new ArrayList<>();
		for (Bar b : bars) {
			if (cityFilter != null && !b.getCity().equalsIgnoreCase(cityFilter)) continue;
			if (statusFilter != null && b.getActivityStatus() != statusFilter) continue;
			if (search != null && !b.getName().toLowerCase().contains(search.toLowerCase())) continue;
			filtered.add(b);
		}

		Comparator<Bar> order = byRating
				? Comparator.comparing(Bar::getAverageRating).thenComparing(Bar::getActiveLikeCount)
				: Comparator.comparing(Bar::getActiveLikeCount).thenComparing(Bar::getAverageRating);
		filtered.sort(order.reversed().thenComparing(Bar::getName));

		List<BarSummary> out = new ArrayList<>();
		for (Bar b : filtered) {
			out.add(BarSummary.from(b, reviewRepository.countByBarId(b.getId())));
		}
		return out;
	}

	@Transactional(readOnly = true)
	public BarDetail getBar(Long id) {
		Bar b = load(id);
		return BarDetail.from(b, reviewRepository.countByBarId(id));
	}

	@Transactional(readOnly = true)
	public List<BarSummary> getBarsByOwner(Long ownerId) {
		ownerComponent.load(ownerId);
		List<BarSummary> out = new ArrayList<>();
		for (Bar b : barRepository.findByOwnerId(ownerId)) {
			out.add(BarSummary.from(b, reviewRepository.countByBarId(b.getId())));
		}
		return out;
	}

	public BarDetail createBar(BarRequest req) {
		Owner owner = ownerComponent.load(req.ownerId);
		Bar b = new Bar();
		b.setOwner(owner);
		copy(req, b);
		b.setAverageRating(0.0);
		b.setActiveLikeCount(0);
		b.setActivityStatus(ActivityStatus.QUIET);
		b.setCreatedAt(Clock.now());
		return BarDetail.from(barRepository.save(b), 0);
	}

	public BarDetail updateBar(Long id, BarRequest req) {
		Bar b = load(id);
		checkOwner(b, req.ownerId);
		copy(req, b);
		return BarDetail.from(barRepository.save(b), reviewRepository.countByBarId(id));
	}

	public MessageResponse deleteBar(Long id, Long ownerId) {
		Bar b = load(id);
		checkOwner(b, ownerId);

		int cancelled = 0;
		for (Reservation r : reservationRepository
				.findByTableBarIdAndReservationDateGreaterThanEqualOrderByReservationDateAsc(id, Clock.today())) {
			if (ReservationComponent.ACTIVE.contains(r.getStatus())) {
				reservationComponent.cancelBySystem(r, true, false);
				cancelled++;
			}
		}
		// Tables (and their reservations), events, likes and reviews go through CascadeType.REMOVE.
		barRepository.delete(b);

		String guests = cancelled == 1
				? "1 upcoming reservation was cancelled and the guest notified."
				: cancelled + " upcoming reservations were cancelled and the guests notified.";
		return new MessageResponse("Bar " + id + " deleted. " + guests);
	}

	/** Recomputes the stored average rating (one decimal place). Called by ReviewComponent. */
	public double recalculateRating(Long barId) {
		Bar b = load(barId);
		Double avg = reviewRepository.averageRatingForBar(barId);
		double rounded = avg == null ? 0.0 : BigDecimal.valueOf(avg).setScale(1, RoundingMode.HALF_UP).doubleValue();
		b.setAverageRating(rounded);
		barRepository.save(b);
		return rounded;
	}

	Bar load(Long id) {
		return barRepository.findById(id)
				.orElseThrow(() -> ApiException.notFound("Bar with id " + id + " does not exist."));
	}

	static void checkOwner(Bar b, Long ownerId) {
		if (!b.getOwner().getId().equals(ownerId)) {
			throw ApiException.forbidden("Owner " + ownerId + " does not own bar " + b.getId() + ".");
		}
	}

	private void copy(BarRequest req, Bar b) {
		String city = MetroManila.canonical(req.city);
		if (city == null) {
			throw ApiException.badRequest("City '" + req.city.trim()
					+ "' is outside Metro Manila. BarHop only accepts bars within Metro Manila.");
		}
		b.setName(req.name.trim());
		b.setDescription(blankToNull(req.description));
		b.setAddress(req.address.trim());
		b.setCity(city);
		b.setOpeningTime(req.openingTime);
		b.setClosingTime(req.closingTime);
		b.setCapacity(req.capacity);
		b.setAmenities(blankToNull(req.amenities));
		b.setImageUrl(blankToNull(req.imageUrl));
	}

	private static String blankToNull(String s) {
		return s == null || s.trim().isEmpty() ? null : s.trim();
	}
}
