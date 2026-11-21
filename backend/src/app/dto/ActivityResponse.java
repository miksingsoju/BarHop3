package app.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import app.db.entity.ActivityStatus;

public class ActivityResponse {
	public Long barId;
	public long activeLikeCount;
	public ActivityStatus activityStatus;
	@JsonFormat(pattern = Formats.DATE_TIME)
	public LocalDateTime asOf;
}
