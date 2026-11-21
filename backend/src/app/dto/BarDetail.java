package app.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import app.db.entity.Bar;

/** Full details of one bar. */
public class BarDetail extends BarSummary {
	public Long ownerId;
	public String ownerName;
	public String description;
	@JsonFormat(pattern = Formats.TIME)
	public LocalTime openingTime;
	@JsonFormat(pattern = Formats.TIME)
	public LocalTime closingTime;
	public Integer capacity;
	public String amenities;
	@JsonFormat(pattern = Formats.DATE_TIME)
	public LocalDateTime createdAt;

	public static BarDetail from(Bar b, long reviewCount) {
		BarDetail d = new BarDetail();
		d.fill(b, reviewCount);
		d.ownerId = b.getOwner().getId();
		d.ownerName = b.getOwner().getFullName();
		d.description = b.getDescription();
		d.openingTime = b.getOpeningTime();
		d.closingTime = b.getClosingTime();
		d.capacity = b.getCapacity();
		d.amenities = b.getAmenities();
		d.createdAt = b.getCreatedAt();
		return d;
	}
}
