package app.components;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import app.Clock;
import app.db.entity.Bar;
import app.db.entity.BarTable;
import app.db.entity.Event;
import app.db.entity.Hopper;
import app.db.entity.Owner;
import app.db.entity.Reservation;
import app.db.entity.ReservationStatus;
import app.db.entity.Review;
import app.db.entity.TableType;
import app.db.entity.TimedLike;
import app.db.repositories.BarRepository;
import app.db.repositories.BarTableRepository;
import app.db.repositories.EventRepository;
import app.db.repositories.HopperRepository;
import app.db.repositories.OwnerRepository;
import app.db.repositories.ReservationRepository;
import app.db.repositories.ReviewRepository;
import app.db.repositories.TimedLikeRepository;

/**
 * Loads demo data on startup when barhop.seed=true and the database is empty.
 *
 * Demo logins:
 *   Owners:  carla@katitaproom.ph / taproom2026,  miguel@poblacionnights.ph / nights2026
 *   Hoppers: paolo.reyes@student.ateneo.edu / katipunan123,
 *            bea.lim@student.ateneo.edu / maginhawa123, jules.cruz@student.ateneo.edu / bgcnights123
 * A "crowd" of extra hoppers (crowd01..crowd32@demo.barhop.ph / crowdpass123) supplies the timed likes,
 * since one hopper can only hold one active like per bar.
 */
@Component
public class DataInitializer {

	private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

	@Value("${barhop.seed:false}")
	private boolean seed;

	@Autowired private OwnerRepository ownerRepository;
	@Autowired private HopperRepository hopperRepository;
	@Autowired private BarRepository barRepository;
	@Autowired private BarTableRepository barTableRepository;
	@Autowired private EventRepository eventRepository;
	@Autowired private ReviewRepository reviewRepository;
	@Autowired private TimedLikeRepository timedLikeRepository;
	@Autowired private ReservationRepository reservationRepository;
	@Autowired private BarComponent barComponent;
	@Autowired private LikeComponent likeComponent;
	@Autowired private PasswordHasher passwordHasher;

	@PostConstruct
	public void seedDatabase() {
		if (!seed || ownerRepository.count() > 0) {
			return;
		}
		log.info("Seeding BarHop demo data...");
		LocalDateTime now = Clock.now();

		// Owners and hoppers
		Owner carla = owner("Carla", "Santos", "carla@katitaproom.ph", "taproom2026", "+639189876543", "Kati Taproom Inc.");
		Owner miguel = owner("Miguel", "Tan", "miguel@poblacionnights.ph", "nights2026", "+639175551234", "Poblacion Nights Corp.");
		Hopper paolo = hopper("Paolo", "Reyes", "paolo.reyes@student.ateneo.edu", "katipunan123", "+639171234567");
		Hopper bea = hopper("Bea", "Lim", "bea.lim@student.ateneo.edu", "maginhawa123", "+639172223333");
		Hopper jules = hopper("Jules", "Cruz", "jules.cruz@student.ateneo.edu", "bgcnights123", "+639173334444");

		// Bars: Katipunan and Maginhawa (QC), Poblacion (Makati), BGC (Taguig)
		Bar kati = bar(carla, "The Kati Taproom", "Neighborhood craft-beer bar a short walk from Gate 3.",
				"312 Katipunan Ave, Loyola Heights", "Quezon City", "18:00", "02:00", 120,
				"Live band on Fridays, Outdoor seating, Darts", "/images/bars/kati-taproom.jpg");
		Bar maginhawa = bar(carla, "Maginhawa Social Club", "Cozy cocktail spot with board games and acoustic sets.",
				"88 Maginhawa St, Teachers Village East", "Quezon City", "17:00", "01:00", 60,
				"Board games, Acoustic nights, Student promos", "/images/bars/maginhawa-social.jpg");
		Bar poblacion = bar(miguel, "Poblacion Rooftop", "Rooftop bar with DJs and a view of the Makati skyline.",
				"5921 Fermina St, Poblacion", "Makati", "19:00", "04:00", 200,
				"Rooftop, Resident DJs, Late-night kitchen", "/images/bars/poblacion-rooftop.jpg");
		Bar bgc = bar(miguel, "BGC Highline", "Sleek lounge for after-work drinks in Bonifacio Global City.",
				"28th St cor. 7th Ave, Bonifacio Global City", "Taguig", "18:00", "03:00", 150,
				"Cocktail bar, Sports screens, VIP booths", "/images/bars/bgc-highline.jpg");

		// Tables: SOLO, GROUP and VIP per bar
		BarTable katiVip = table(kati, "VIP Booth 1", TableType.VIP, 8, 5000);
		BarTable katiGroup = table(kati, "Group Table A", TableType.GROUP, 6, 1500);
		table(kati, "Counter Seat 1", TableType.SOLO, 1, 0);
		table(kati, "Counter Seat 2", TableType.SOLO, 1, 0);
		table(maginhawa, "Window Nook", TableType.GROUP, 4, 800);
		table(maginhawa, "Long Table", TableType.GROUP, 10, 2500);
		table(maginhawa, "Bar Stool 1", TableType.SOLO, 1, 0);
		table(poblacion, "Skyline VIP", TableType.VIP, 12, 10000);
		table(poblacion, "Rooftop Lounge 1", TableType.GROUP, 6, 3000);
		table(poblacion, "Rail Seat 1", TableType.SOLO, 2, 500);
		table(bgc, "Highline VIP", TableType.VIP, 10, 8000);
		table(bgc, "High Top 1", TableType.GROUP, 4, 1200);
		table(bgc, "Bar Seat 1", TableType.SOLO, 1, 0);

		// Two upcoming events
		LocalDate today = Clock.today();
		event(kati, "Post-Finals Acoustic Night", "Local student bands, 2-for-1 drafts until 10 PM.",
				today.plusDays(7).atTime(20, 0), today.plusDays(8).atTime(1, 0), 200);
		event(poblacion, "Rooftop DJ Takeover", "Guest DJs from Manila's house scene all night.",
				today.plusDays(3).atTime(21, 0), today.plusDays(4).atTime(3, 0), 500);

		// Reviews, then recompute ratings
		review(paolo, kati, 5, "Great music, quick service, fair prices.", today.minusDays(7));
		review(bea, kati, 4, "Good beer list. Gets crowded after 11.", today.minusDays(5));
		review(jules, kati, 4, null, today.minusDays(2));
		review(bea, maginhawa, 5, "Perfect for a chill night with the org.", today.minusDays(10));
		review(paolo, poblacion, 4, "Amazing view, pricey drinks.", today.minusDays(14));
		review(jules, bgc, 3, "Nice place but slow service on a Friday.", today.minusDays(3));
		for (Bar b : new Bar[] { kati, maginhawa, poblacion, bgc }) {
			barComponent.recalculateRating(b.getId());
		}

		// Timed likes so bars show different statuses:
		// Poblacion 32 (PACKED), Kati 18 (LIVELY), Maginhawa 7 (MODERATE), BGC 2 (QUIET).
		Hopper[] crowd = new Hopper[32];
		for (int i = 0; i < crowd.length; i++) {
			String n = String.format("%02d", i + 1);
			crowd[i] = hopper("Crowd", "Member " + n, "crowd" + n + "@demo.barhop.ph", "crowdpass123",
					"+6391700000" + n);
		}
		likes(poblacion, crowd, 32, now);
		likes(kati, crowd, 18, now);
		likes(maginhawa, crowd, 7, now);
		likes(bgc, crowd, 2, now);
		for (Bar b : new Bar[] { kati, maginhawa, poblacion, bgc }) {
			likeComponent.refreshBarActivity(b.getId());
		}

		// One CONFIRMED booking starting within 2 hours (so the reminder job fires) and one PENDING later.
		LocalDateTime soon = now.plusMinutes(75).truncatedTo(ChronoUnit.HOURS).plusMinutes(30);
		reservation(paolo, katiVip, soon, 2, 6, ReservationStatus.CONFIRMED);
		reservation(paolo, katiGroup, soon.plusHours(2), 2, 4, ReservationStatus.PENDING);

		log.info("Seeding done: 2 owners, {} hoppers, 4 bars.", 3 + crowd.length);
	}

	private Owner owner(String first, String last, String email, String password, String phone, String business) {
		Owner o = new Owner();
		o.setFirstName(first);
		o.setLastName(last);
		o.setEmail(email);
		o.setPasswordHash(passwordHasher.hash(password));
		o.setPhoneNumber(phone);
		o.setBusinessName(business);
		o.setCreatedAt(Clock.now());
		return ownerRepository.save(o);
	}

	private Hopper hopper(String first, String last, String email, String password, String phone) {
		Hopper h = new Hopper();
		h.setFirstName(first);
		h.setLastName(last);
		h.setEmail(email);
		h.setPasswordHash(passwordHasher.hash(password));
		h.setPhoneNumber(phone);
		h.setCreatedAt(Clock.now());
		return hopperRepository.save(h);
	}

	private Bar bar(Owner owner, String name, String description, String address, String city, String open,
			String close, int capacity, String amenities, String imageUrl) {
		Bar b = new Bar();
		b.setOwner(owner);
		b.setName(name);
		b.setDescription(description);
		b.setAddress(address);
		b.setCity(city);
		b.setOpeningTime(LocalTime.parse(open));
		b.setClosingTime(LocalTime.parse(close));
		b.setCapacity(capacity);
		b.setAmenities(amenities);
		b.setImageUrl(imageUrl);
		b.setCreatedAt(Clock.now());
		return barRepository.save(b);
	}

	private BarTable table(Bar bar, String label, TableType type, int capacity, int rate) {
		BarTable t = new BarTable();
		t.setBar(bar);
		t.setLabel(label);
		t.setTableType(type);
		t.setCapacity(capacity);
		t.setRate(BigDecimal.valueOf(rate));
		t.setActive(Boolean.TRUE);
		return barTableRepository.save(t);
	}

	private void event(Bar bar, String title, String description, LocalDateTime start, LocalDateTime end, int cover) {
		Event e = new Event();
		e.setBar(bar);
		e.setTitle(title);
		e.setDescription(description);
		e.setStartDateTime(start);
		e.setEndDateTime(end);
		e.setCoverCharge(BigDecimal.valueOf(cover));
		eventRepository.save(e);
	}

	private void review(Hopper hopper, Bar bar, int rating, String comment, LocalDate visitDate) {
		Review r = new Review();
		r.setHopper(hopper);
		r.setBar(bar);
		r.setRating(rating);
		r.setComment(comment);
		r.setVisitDate(visitDate);
		r.setCreatedAt(visitDate.plusDays(1).atTime(10, 0));
		r.setUpdatedAt(r.getCreatedAt());
		reviewRepository.save(r);
	}

	/** count likes from the first count crowd members, spread over the last 85 minutes (some about to expire). */
	private void likes(Bar bar, Hopper[] crowd, int count, LocalDateTime now) {
		for (int i = 0; i < count; i++) {
			LocalDateTime likedAt = now.minusMinutes((i * 85L) / Math.max(1, count - 1));
			TimedLike l = new TimedLike();
			l.setHopper(crowd[i]);
			l.setBar(bar);
			l.setLikedAt(likedAt);
			l.setExpiresAt(likedAt.plusMinutes(TimedLike.DURATION_MINUTES));
			timedLikeRepository.save(l);
		}
	}

	private void reservation(Hopper hopper, BarTable table, LocalDateTime start, int hours, int guests,
			ReservationStatus status) {
		Reservation r = new Reservation();
		r.setHopper(hopper);
		r.setTable(table);
		r.setReservationDate(start.toLocalDate());
		r.setStartTime(start.toLocalTime());
		r.setEndTime(start.toLocalTime().plusHours(hours));
		r.setGuestCount(guests);
		r.setContactName(hopper.getFullName());
		r.setContactNumber(hopper.getPhoneNumber());
		r.setAgreedToPolicy(Boolean.TRUE);
		r.setStatus(status);
		r.setReminderSent(Boolean.FALSE);
		r.setCreatedAt(Clock.now());
		reservationRepository.save(r);
	}
}
