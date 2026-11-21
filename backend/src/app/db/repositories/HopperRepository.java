package app.db.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.db.entity.Hopper;

@Repository
public interface HopperRepository extends JpaRepository<Hopper, Long> {

	Optional<Hopper> findByEmailIgnoreCase(String email);

	boolean existsByEmailIgnoreCase(String email);
}
