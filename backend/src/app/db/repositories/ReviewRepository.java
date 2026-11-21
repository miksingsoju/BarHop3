package app.db.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import app.db.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

	List<Review> findByBarIdOrderByCreatedAtDesc(Long barId);

	List<Review> findByHopperIdOrderByCreatedAtDesc(Long hopperId);

	boolean existsByHopperIdAndBarId(Long hopperId, Long barId);

	long countByBarId(Long barId);

	@Query("SELECT AVG(r.rating) FROM Review r WHERE r.bar.id = :barId")
	Double averageRatingForBar(@Param("barId") Long barId);
}
