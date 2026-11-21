package app.db.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.db.entity.Owner;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {

	Optional<Owner> findByEmailIgnoreCase(String email);

	boolean existsByEmailIgnoreCase(String email);
}
