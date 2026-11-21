package app.db.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import app.db.entity.TimedLike;

@Repository
public interface TimedLikeRepository extends JpaRepository<TimedLike, Long> {

	long countByBarIdAndExpiresAtAfter(Long barId, LocalDateTime now);

	boolean existsByHopperIdAndBarIdAndExpiresAtAfter(Long hopperId, Long barId, LocalDateTime now);

	Optional<TimedLike> findFirstByHopperIdAndBarIdAndExpiresAtAfter(Long hopperId, Long barId, LocalDateTime now);

	List<TimedLike> findByBarIdAndExpiresAtAfter(Long barId, LocalDateTime now);

	List<TimedLike> findByHopperIdAndExpiresAtAfter(Long hopperId, LocalDateTime now);

	List<TimedLike> findByHopperId(Long hopperId);

	List<TimedLike> findByExpiresAtBefore(LocalDateTime now);

	@Modifying
	@Transactional
	void deleteByHopperId(Long hopperId);
}
