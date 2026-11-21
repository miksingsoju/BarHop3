package app.db.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.db.entity.BarTable;

@Repository
public interface BarTableRepository extends JpaRepository<BarTable, Long> {

	List<BarTable> findByBarIdOrderByLabelAsc(Long barId);

	List<BarTable> findByBarIdAndActiveTrueAndCapacityGreaterThanEqual(Long barId, Integer guests);

	boolean existsByBarIdAndLabelIgnoreCase(Long barId, String label);
}
