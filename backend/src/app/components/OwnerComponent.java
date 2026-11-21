package app.components;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import app.Clock;
import app.db.entity.Owner;
import app.db.repositories.BarRepository;
import app.db.repositories.HopperRepository;
import app.db.repositories.OwnerRepository;
import app.dto.LoginRequest;
import app.dto.MessageResponse;
import app.dto.OwnerRequest;
import app.dto.OwnerResponse;
import app.dto.OwnerUpdateRequest;
import app.rest.errorhandlers.ApiException;

/** Bar Owner accounts. Mirrors HopperComponent. */
@Component
@Transactional
public class OwnerComponent {

	@Autowired private OwnerRepository ownerRepository;
	@Autowired private HopperRepository hopperRepository;
	@Autowired private BarRepository barRepository;
	@Autowired private PasswordHasher passwordHasher;

	public OwnerResponse registerOwner(OwnerRequest req) {
		String email = HopperComponent.normalizeEmail(req.email);
		if (hopperRepository.existsByEmailIgnoreCase(email) || ownerRepository.existsByEmailIgnoreCase(email)) {
			throw ApiException.conflict("An account with email '" + email + "' already exists.");
		}
		Owner o = new Owner();
		o.setFirstName(req.firstName.trim());
		o.setLastName(req.lastName.trim());
		o.setEmail(email);
		o.setPasswordHash(passwordHasher.hash(req.password));
		o.setPhoneNumber(req.phoneNumber);
		o.setBusinessName(blankToNull(req.businessName));
		o.setCreatedAt(Clock.now());
		return OwnerResponse.from(ownerRepository.save(o), 0);
	}

	@Transactional(readOnly = true)
	public OwnerResponse login(LoginRequest req) {
		Owner o = ownerRepository.findByEmailIgnoreCase(HopperComponent.normalizeEmail(req.email))
				.orElseThrow(() -> ApiException.unauthorized("Invalid email or password."));
		if (!passwordHasher.matches(req.password, o.getPasswordHash())) {
			throw ApiException.unauthorized("Invalid email or password.");
		}
		return toResponse(o);
	}

	@Transactional(readOnly = true)
	public List<OwnerResponse> getAllOwners() {
		List<OwnerResponse> out = new ArrayList<>();
		for (Owner o : ownerRepository.findAll()) {
			out.add(toResponse(o));
		}
		return out;
	}

	@Transactional(readOnly = true)
	public OwnerResponse getOwner(Long id) {
		return toResponse(load(id));
	}

	public OwnerResponse updateOwner(Long id, OwnerUpdateRequest req) {
		Owner o = load(id);
		if (req.firstName != null) o.setFirstName(req.firstName.trim());
		if (req.lastName != null) o.setLastName(req.lastName.trim());
		if (req.phoneNumber != null) o.setPhoneNumber(req.phoneNumber);
		if (req.businessName != null) o.setBusinessName(blankToNull(req.businessName));
		return toResponse(ownerRepository.save(o));
	}

	public MessageResponse deleteOwner(Long id) {
		Owner o = load(id);
		long bars = barRepository.countByOwnerId(id);
		if (bars > 0) {
			throw ApiException.conflict("Owner " + id + " still has " + TimeFormat.plural(bars, "bar listing", "bar listings")
					+ ". Delete " + (bars == 1 ? "it" : "them") + " first.");
		}
		ownerRepository.delete(o);
		return new MessageResponse("Owner " + id + " deleted.");
	}

	Owner load(Long id) {
		return ownerRepository.findById(id)
				.orElseThrow(() -> ApiException.notFound("Owner with id " + id + " does not exist."));
	}

	private OwnerResponse toResponse(Owner o) {
		return OwnerResponse.from(o, barRepository.countByOwnerId(o.getId()));
	}

	private static String blankToNull(String s) {
		return s == null || s.trim().isEmpty() ? null : s.trim();
	}
}
