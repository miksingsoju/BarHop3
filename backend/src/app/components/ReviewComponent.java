package app.components;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import app.Clock;
import app.db.entity.Bar;
import app.db.entity.Hopper;
import app.db.entity.Review;
import app.db.repositories.BarRepository;
import app.db.repositories.HopperRepository;
import app.db.repositories.ReviewRepository;
import app.dto.MessageResponse;
import app.dto.ReviewRequest;
import app.dto.ReviewResponse;
import app.rest.errorhandlers.ApiException;

/** Reviews and ratings (never expire). Keeps each bar's average rating correct. */
@Component
@Transactional
public class ReviewComponent {

	@Autowired private ReviewRepository reviewRepository;
	@Autowired private HopperRepository hopperRepository;
	@Autowired private BarRepository barRepository;
	@Autowired private BarComponent barComponent;

	public ReviewResponse createReview(ReviewRequest req) {
		if (req.barId == null) {
			throw ApiException.fieldError("barId", "must not be null");
		}
		Hopper hopper = hopperRepository.findById(req.hopperId)
				.orElseThrow(() -> ApiException.notFound("Hopper with id " + req.hopperId + " does not exist."));
		Bar bar = barRepository.findById(req.barId)
				.orElseThrow(() -> ApiException.notFound("Bar with id " + req.barId + " does not exist."));
		if (reviewRepository.existsByHopperIdAndBarId(hopper.getId(), bar.getId())) {
			throw ApiException.conflict("You have already reviewed this bar. Edit your existing review instead.");
		}
		Review r = new Review();
		r.setHopper(hopper);
		r.setBar(bar);
		r.setRating(req.rating);
		r.setComment(blankToNull(req.comment));
		r.setVisitDate(req.visitDate);
		r.setCreatedAt(Clock.now());
		r.setUpdatedAt(r.getCreatedAt());
		r = reviewRepository.saveAndFlush(r);
		barComponent.recalculateRating(bar.getId());
		return ReviewResponse.from(r);
	}

	public ReviewResponse updateReview(Long id, ReviewRequest req) {
		Review r = load(id);
		checkAuthor(r, req.hopperId);
		r.setRating(req.rating);
		r.setComment(blankToNull(req.comment));
		r.setVisitDate(req.visitDate);
		r.setUpdatedAt(Clock.now());
		r = reviewRepository.saveAndFlush(r);
		barComponent.recalculateRating(r.getBar().getId());
		return ReviewResponse.from(r);
	}

	public MessageResponse deleteReview(Long id, Long hopperId) {
		Review r = load(id);
		checkAuthor(r, hopperId);
		Long barId = r.getBar().getId();
		reviewRepository.delete(r);
		reviewRepository.flush();
		double avg = barComponent.recalculateRating(barId);
		return new MessageResponse("Review " + id + " deleted.").with("barId", barId).with("newAverageRating", avg);
	}

	@Transactional(readOnly = true)
	public List<ReviewResponse> getReviews(Long barId, Long hopperId) {
		List<Review> list;
		if (barId != null) {
			if (!barRepository.existsById(barId)) {
				throw ApiException.notFound("Bar with id " + barId + " does not exist.");
			}
			list = reviewRepository.findByBarIdOrderByCreatedAtDesc(barId);
		} else if (hopperId != null) {
			if (!hopperRepository.existsById(hopperId)) {
				throw ApiException.notFound("Hopper with id " + hopperId + " does not exist.");
			}
			list = reviewRepository.findByHopperIdOrderByCreatedAtDesc(hopperId);
		} else {
			throw ApiException.badRequest("Provide either barId or hopperId.");
		}
		List<ReviewResponse> out = new ArrayList<>();
		for (Review r : list) {
			out.add(ReviewResponse.from(r));
		}
		return out;
	}

	@Transactional(readOnly = true)
	public ReviewResponse getReview(Long id) {
		return ReviewResponse.from(load(id));
	}

	private Review load(Long id) {
		return reviewRepository.findById(id)
				.orElseThrow(() -> ApiException.notFound("Review with id " + id + " does not exist."));
	}

	private static void checkAuthor(Review r, Long hopperId) {
		if (!r.getHopper().getId().equals(hopperId)) {
			throw ApiException.forbidden("Review " + r.getId() + " does not belong to hopper " + hopperId + ".");
		}
	}

	private static String blankToNull(String s) {
		return s == null || s.trim().isEmpty() ? null : s.trim();
	}
}
