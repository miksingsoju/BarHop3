package app.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import app.Clock;
import app.db.entity.Hopper;
import app.db.entity.Reservation;
import app.db.entity.ReservationStatus;
import app.db.entity.Review;
import app.db.entity.TimedLike;
import app.db.repositories.HopperRepository;
import app.db.repositories.OwnerRepository;
import app.db.repositories.ReservationRepository;
import app.db.repositories.ReviewRepository;
import app.db.repositories.TimedLikeRepository;
import app.dto.HopperRequest;
import app.dto.HopperResponse;
import app.dto.HopperUpdateRequest;
import app.dto.LoginRequest;
import app.dto.MessageResponse;
import app.rest.errorhandlers.ApiException;

/** Bar Hopper accounts: registration, login, profile updates and deletion. */
@Component
@Transactional
public class HopperComponent {

	@Autowired private HopperRepository hopperRepository;
	@Autowired private OwnerRepository ownerRepository;
	@Autowired private ReservationRepository reservationRepository;
	@Autowired private ReviewRepository reviewRepository;
	@Autowired private TimedLikeRepository timedLikeRepository;
	@Autowired private ReservationComponent reservationComponent;
	@Autowired private BarComponent barComponent;
	@Autowired private LikeComponent likeComponent;
	@Autowired private PasswordHasher passwordHasher;

	public HopperResponse registerHopper(HopperRequest req) {
		String email = normalizeEmail(req.email);
		if (hopperRepository.existsByEmailIgnoreCase(email) || ownerRepository.existsByEmailIgnoreCase(email)) {
			throw ApiException.conflict("An account with email '" + email + "' already exists.");
		}
		Hopper h = new Hopper();
		h.setFirstName(req.firstName.trim());
		h.setLastName(req.lastName.trim());
		h.setEmail(email);
		h.setPasswordHash(passwordHasher.hash(req.password));
		h.setPhoneNumber(req.phoneNumber);
		h.setCreatedAt(Clock.now());
		return HopperResponse.from(hopperRepository.save(h));
	}

	@Transactional(readOnly = true)
	public HopperResponse login(LoginRequest req) {
		Hopper h = hopperRepository.findByEmailIgnoreCase(normalizeEmail(req.email))
				.orElseThrow(() -> ApiException.unauthorized("Invalid email or password."));
		if (!passwordHasher.matches(req.password, h.getPasswordHash())) {
			throw ApiException.unauthorized("Invalid email or password.");
		}
		return HopperResponse.from(h);
	}

	@Transactional(readOnly = true)
	public List<HopperResponse> getAllHoppers() {
		List<HopperResponse> out = new ArrayList<>();
		for (Hopper h : hopperRepository.findAll()) {
			out.add(HopperResponse.from(h));
		}
		return out;
	}

	@Transactional(readOnly = true)
	public HopperResponse getHopper(Long id) {
		return HopperResponse.from(load(id));
	}

	public HopperResponse updateHopper(Long id, HopperUpdateRequest req) {
		Hopper h = load(id);
		if (req.firstName != null) h.setFirstName(req.firstName.trim());
		if (req.lastName != null) h.setLastName(req.lastName.trim());
		if (req.phoneNumber != null) h.setPhoneNumber(req.phoneNumber);
		return HopperResponse.from(hopperRepository.save(h));
	}

	public MessageResponse deleteHopper(Long id) {
		Hopper h = load(id);

		// 1. Cancel upcoming bookings (the owner is texted).
		int cancelled = 0;
		List<Reservation> active = reservationRepository.findByHopperIdAndStatusIn(id,
				Arrays.asList(ReservationStatus.PENDING, ReservationStatus.CONFIRMED));
		for (Reservation r : active) {
			if (!r.getReservationDate().isBefore(Clock.today())) {
				reservationComponent.cancelBySystem(r, false, true);
				cancelled++;
			}
		}

		// 2. Remove likes and refresh the activity of those bars.
		Set<Long> likedBars = new LinkedHashSet<>();
		for (TimedLike l : timedLikeRepository.findByHopperId(id)) {
			likedBars.add(l.getBar().getId());
		}
		timedLikeRepository.deleteByHopperId(id);
		for (Long barId : likedBars) {
			likeComponent.refreshBarActivity(barId);
		}

		// 3. Remove reviews and recalculate those bars' ratings.
		Set<Long> reviewedBars = new LinkedHashSet<>();
		List<Review> reviews = reviewRepository.findByHopperIdOrderByCreatedAtDesc(id);
		for (Review r : reviews) {
			reviewedBars.add(r.getBar().getId());
		}
		reviewRepository.deleteAll(reviews);
		reviewRepository.flush();
		for (Long barId : reviewedBars) {
			barComponent.recalculateRating(barId);
		}

		// 4. Remove booking history, then the account.
		reservationRepository.deleteAll(reservationRepository.findByHopperId(id));
		hopperRepository.delete(h);

		return new MessageResponse("Hopper " + id + " deleted. "
				+ TimeFormat.plural(cancelled, "upcoming reservation was", "upcoming reservations were") + " cancelled.");
	}

	Hopper load(Long id) {
		return hopperRepository.findById(id)
				.orElseThrow(() -> ApiException.notFound("Hopper with id " + id + " does not exist."));
	}

	static String normalizeEmail(String email) {
		return email == null ? null : email.trim().toLowerCase();
	}
}
