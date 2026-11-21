package app.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import app.db.entity.Event;

public class EventResponse {
	public Long id;
	public Long barId;
	public String barName;
	public String title;
	public String description;
	@JsonFormat(pattern = Formats.DATE_TIME)
	public LocalDateTime startDateTime;
	@JsonFormat(pattern = Formats.DATE_TIME)
	public LocalDateTime endDateTime;
	public BigDecimal coverCharge;

	public static EventResponse from(Event e) {
		EventResponse r = new EventResponse();
		r.id = e.getId();
		r.barId = e.getBar().getId();
		r.barName = e.getBar().getName();
		r.title = e.getTitle();
		r.description = e.getDescription();
		r.startDateTime = e.getStartDateTime();
		r.endDateTime = e.getEndDateTime();
		r.coverCharge = Formats.plain(e.getCoverCharge());
		return r;
	}
}
