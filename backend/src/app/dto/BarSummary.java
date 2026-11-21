package app.dto;

import app.db.entity.ActivityStatus;
import app.db.entity.Bar;

/** One bar in a listing (home page feed, owner dashboard). */
public class BarSummary {
	public Long id;
	public String name;
	public String city;
	public String address;
	public Double averageRating;
	public long reviewCount;
	public Integer activeLikeCount;
	public ActivityStatus activityStatus;
	public String imageUrl;

	protected void fill(Bar b, long reviewCount) {
		id = b.getId();
		name = b.getName();
		city = b.getCity();
		address = b.getAddress();
		averageRating = b.getAverageRating();
		this.reviewCount = reviewCount;
		activeLikeCount = b.getActiveLikeCount();
		activityStatus = b.getActivityStatus();
		imageUrl = b.getImageUrl();
	}

	public static BarSummary from(Bar b, long reviewCount) {
		BarSummary s = new BarSummary();
		s.fill(b, reviewCount);
		return s;
	}
}
