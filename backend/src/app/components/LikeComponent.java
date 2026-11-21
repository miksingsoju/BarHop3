package app.components;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import app.Clock;
import app.db.entity.ActivityStatus;
import app.db.entity.Bar;
import app.db.entity.Hopper;
import app.db.entity.TimedLike;
import app.db.repositories.BarRepository;
import app.db.repositories.HopperRepository;
import app.db.repositories.TimedLikeRepository;
import app.dto.ActivityResponse;
import app.dto.LikeRequest;
import app.dto.LikeResponse;
import app.dto.MessageResponse;
import app.rest.errorhandlers.ApiException;

/** Timed likes (90 minutes) and each bar's live activity status. */
@Component
@Transactional
public class LikeComponent {

	@Autowired private TimedLikeRepository timedLikeRepository;
	@Autowired private HopperRepository hopperRepository;
	@Autowired private BarRepository barRepository;

	public LikeResponse likeBar(LikeRequest req) {
		Hopper hopper = hopperRepository.findById(req.hopperId)
				.orElseThrow(() -> ApiException.notFound("Hopper with id " + req.hopperId + " does not exist."));
		Bar bar = loadBar(req.barId);
		LocalDateTime now = Clock.now();

		if (timedLikeRepository.existsByHopperIdAndBarIdAndExpiresAtAfter(hopper.getId(), bar.getId(), now)) {
			TimedLike existing = timedLikeRepository
					.findFirstByHopperIdAndBarIdAndExpiresAtAfter(hopper.getId(), bar.getId(), now).get();
			throw ApiException.conflict("You already have an active like on this bar. It expires at "
					+ TimeFormat.hm(existing.getExpiresAt().toLocalTime()) + ".");
		}

		TimedLike like = new TimedLike();
		like.setHopper(hopper);
		like.setBar(bar);
		like.setLikedAt(now);
		like.setExpiresAt(now.plusMinutes(TimedLike.DURATION_MINUTES));
		like = timedLikeRepository.saveAndFlush(like);

		refreshBarActivity(bar);
		return LikeResponse.from(like);
	}

	public MessageResponse removeLike(Long id, Long hopperId) {
		TimedLike like = timedLikeRepository.findById(id)
				.filter(l -> l.getExpiresAt().isAfter(Clock.now()))
				.orElseThrow(() -> ApiException.notFound("Like with id " + id + " does not exist or has already expired."));
		if (!like.getHopper().getId().equals(hopperId)) {
			throw ApiException.forbidden("Like " + id + " does not belong to hopper " + hopperId + ".");
		}
		Bar bar = like.getBar();
		timedLikeRepository.delete(like);
		timedLikeRepository.flush();
		refreshBarActivity(bar);
		return new MessageResponse("Like " + id + " removed.")
				.with("barId", bar.getId())
				.with("barActiveLikeCount", bar.getActiveLikeCount())
				.with("barActivityStatus", bar.getActivityStatus());
	}

	@Transactional(readOnly = true)
	public List<LikeResponse> getActiveLikes(Long barId, Long hopperId) {
		LocalDateTime now = Clock.now();
		List<TimedLike> likes;
		if (barId != null) {
			loadBar(barId);
			likes = timedLikeRepository.findByBarIdAndExpiresAtAfter(barId, now);
		} else if (hopperId != null) {
			if (!hopperRepository.existsById(hopperId)) {
				throw ApiException.notFound("Hopper with id " + hopperId + " does not exist.");
			}
			likes = timedLikeRepository.findByHopperIdAndExpiresAtAfter(hopperId, now);
		} else {
			throw ApiException.badRequest("Provide either barId or hopperId.");
		}
		List<LikeResponse> out = new ArrayList<>();
		for (TimedLike l : likes) {
			out.add(LikeResponse.from(l));
		}
		return out;
	}

	@Transactional(readOnly = true)
	public ActivityResponse getActivity(Long barId) {
		loadBar(barId);
		LocalDateTime now = Clock.now();
		long count = timedLikeRepository.countByBarIdAndExpiresAtAfter(barId, now);
		ActivityResponse r = new ActivityResponse();
		r.barId = barId;
		r.activeLikeCount = count;
		r.activityStatus = ActivityStatus.fromLikeCount(count);
		r.asOf = now;
		return r;
	}

	/** Recounts active likes and stores the bar's count and status. */
	public void refreshBarActivity(Bar bar) {
		long count = timedLikeRepository.countByBarIdAndExpiresAtAfter(bar.getId(), Clock.now());
		bar.setActiveLikeCount((int) count);
		bar.setActivityStatus(ActivityStatus.fromLikeCount(count));
		barRepository.save(bar);
	}

	public void refreshBarActivity(Long barId) {
		barRepository.findById(barId).ifPresent(this::refreshBarActivity);
	}

	/** Scheduler: removes expired likes. Returns {likes removed, bars updated}. */
	public int[] expireLikes() {
		List<TimedLike> expired = timedLikeRepository.findByExpiresAtBefore(Clock.now().plusSeconds(1));
		if (expired.isEmpty()) {
			return new int[] { 0, 0 };
		}
		Set<Long> barIds = new LinkedHashSet<>();
		for (TimedLike l : expired) {
			barIds.add(l.getBar().getId());
		}
		timedLikeRepository.deleteInBatch(expired);
		for (Long barId : barIds) {
			refreshBarActivity(barId);
		}
		return new int[] { expired.size(), barIds.size() };
	}

	private Bar loadBar(Long id) {
		return barRepository.findById(id)
				.orElseThrow(() -> ApiException.notFound("Bar with id " + id + " does not exist."));
	}
}
