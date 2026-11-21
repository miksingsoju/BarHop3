package app.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import app.db.entity.Review;

public class ReviewResponse {
	public Long id;
	public Long hopperId;
	public String hopperName;
	public Long barId;
	public String barName;
	public Integer rating;
	public String comment;
	public LocalDate visitDate;
	@JsonFormat(pattern = Formats.DATE_TIME)
	public LocalDateTime createdAt;
	@JsonFormat(pattern = Formats.DATE_TIME)
	public LocalDateTime updatedAt;

	public static ReviewResponse from(Review rv) {
		ReviewResponse r = new ReviewResponse();
		r.id = rv.getId();
		r.hopperId = rv.getHopper().getId();
		r.hopperName = rv.getHopper().getFullName();
		r.barId = rv.getBar().getId();
		r.barName = rv.getBar().getName();
		r.rating = rv.getRating();
		r.comment = rv.getComment();
		r.visitDate = rv.getVisitDate();
		r.createdAt = rv.getCreatedAt();
		r.updatedAt = rv.getUpdatedAt();
		return r;
	}
}
