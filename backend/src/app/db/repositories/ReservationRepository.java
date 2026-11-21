package app.db.repositories;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.db.entity.Reservation;
import app.db.entity.ReservationStatus;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

	/** Same-day overlap check: an active booking starts before the new one ends and ends after it starts. */
	boolean existsByTableIdAndReservationDateAndStartTimeLessThanAndEndTimeGreaterThanAndStatusIn(
			Long tableId, LocalDate date, LocalTime end, LocalTime start, Collection<ReservationStatus> statuses);

	/** Active bookings around a date, used for the overlap check across midnight. */
	List<Reservation> findByTableIdAndReservationDateBetweenAndStatusIn(
			Long tableId, LocalDate from, LocalDate to, Collection<ReservationStatus> statuses);

	List<Reservation> findByHopperIdOrderByReservationDateDescStartTimeDesc(Long hopperId);

	List<Reservation> findByHopperIdAndStatusIn(Long hopperId, Collection<ReservationStatus> statuses);

	List<Reservation> findByHopperId(Long hopperId);

	List<Reservation> findByTableBarIdAndReservationDateOrderByStartTimeAsc(Long barId, LocalDate date);

	List<Reservation> findByTableBarIdAndReservationDateGreaterThanEqualOrderByReservationDateAsc(Long barId, LocalDate today);

	long countByTableIdAndReservationDateGreaterThanEqualAndStatusIn(
			Long tableId, LocalDate today, Collection<ReservationStatus> statuses);

	List<Reservation> findByStatusAndReminderSentFalseAndReservationDate(ReservationStatus status, LocalDate date);

	List<Reservation> findByReservationDateBeforeAndStatusIn(LocalDate today, Collection<ReservationStatus> statuses);
}
