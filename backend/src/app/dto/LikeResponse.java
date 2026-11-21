package app.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import app.db.entity.ActivityStatus;
import app.db.entity.TimedLike;

public class LikeResponse {
	public Long id;
	public Long hopperId;
	public Long barId;
	public String barName;
	@JsonFormat(pattern = Formats.DATE_TIME)
	public LocalDateTime likedAt;
	@JsonFormat(pattern = Formats.DATE_TIME)
	public LocalDateTime expiresAt;
	public Integer barActiveLikeCount;
	public ActivityStatus barActivityStatus;

	public static LikeResponse from(TimedLike l) {
		LikeResponse r = new LikeResponse();
		r.id = l.getId();
		r.hopperId = l.getHopper().getId();
		r.barId = l.getBar().getId();
		r.barName = l.getBar().getName();
		r.likedAt = l.getLikedAt();
		r.expiresAt = l.getExpiresAt();
		r.barActiveLikeCount = l.getBar().getActiveLikeCount();
		r.barActivityStatus = l.getBar().getActivityStatus();
		return r;
	}
}
