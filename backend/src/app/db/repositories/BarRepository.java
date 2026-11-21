package app.db.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.db.entity.ActivityStatus;
import app.db.entity.Bar;

@Repository
public interface BarRepository extends JpaRepository<Bar, Long> {

	List<Bar> findAllByOrderByActiveLikeCountDesc();

	List<Bar> findByCityIgnoreCase(String city);

	List<Bar> findByNameContainingIgnoreCase(String name);

	List<Bar> findByActivityStatus(ActivityStatus status);

	List<Bar> findByOwnerId(Long ownerId);

	long countByOwnerId(Long ownerId);
}
