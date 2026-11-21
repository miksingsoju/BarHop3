package app.db.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.db.entity.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

	List<Event> findByBarIdOrderByStartDateTimeAsc(Long barId);

	List<Event> findByBarIdAndStartDateTimeAfterOrderByStartDateTimeAsc(Long barId, LocalDateTime now);

	List<Event> findByStartDateTimeAfterOrderByStartDateTimeAsc(LocalDateTime now);

	List<Event> findAllByOrderByStartDateTimeAsc();
}
