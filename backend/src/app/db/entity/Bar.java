package app.db.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/** A bar or club listed on BarHop. Stores computed rating/activity so listings load quickly. */
@Entity
public class Bar {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@ManyToOne(optional = false)
	private Owner owner;

	@NotBlank
	@Size(max = 100)
	@Column(nullable = false, length = 100)
	private String name;

	@Size(max = 1000)
	@Column(length = 1000)
	private String description;

	@NotBlank
	@Size(max = 200)
	@Column(nullable = false, length = 200)
	private String address;

	/** One of the 17 Metro Manila LGUs (checked in BarComponent). */
	@NotBlank
	@Column(nullable = false, length = 50)
	private String city;

	@NotNull
	@Column(nullable = false)
	private LocalTime openingTime;

	/** May be after midnight (e.g. 02:00 for an 18:00 opening). */
	@NotNull
	@Column(nullable = false)
	private LocalTime closingTime;

	@NotNull
	@Min(1)
	@Max(5000)
	@Column(nullable = false)
	private Integer capacity;

	@Size(max = 500)
	@Column(length = 500)
	private String amenities;

	@Size(max = 300)
	@Column(length = 300)
	private String imageUrl;

	@Column(nullable = false)
	private Double averageRating = 0.0;

	@Column(nullable = false)
	private Integer activeLikeCount = 0;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	private ActivityStatus activityStatus = ActivityStatus.QUIET;

	@NotNull
	@Column(nullable = false)
	private LocalDateTime createdAt;

	@OneToMany(mappedBy = "bar", cascade = CascadeType.REMOVE)
	private List<BarTable> tables = new ArrayList<>();

	@OneToMany(mappedBy = "bar", cascade = CascadeType.REMOVE)
	private List<Event> events = new ArrayList<>();

	@OneToMany(mappedBy = "bar", cascade = CascadeType.REMOVE)
	private List<TimedLike> likes = new ArrayList<>();

	@OneToMany(mappedBy = "bar", cascade = CascadeType.REMOVE)
	private List<Review> reviews = new ArrayList<>();

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public Owner getOwner() { return owner; }
	public void setOwner(Owner owner) { this.owner = owner; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	public String getAddress() { return address; }
	public void setAddress(String address) { this.address = address; }
	public String getCity() { return city; }
	public void setCity(String city) { this.city = city; }
	public LocalTime getOpeningTime() { return openingTime; }
	public void setOpeningTime(LocalTime openingTime) { this.openingTime = openingTime; }
	public LocalTime getClosingTime() { return closingTime; }
	public void setClosingTime(LocalTime closingTime) { this.closingTime = closingTime; }
	public Integer getCapacity() { return capacity; }
	public void setCapacity(Integer capacity) { this.capacity = capacity; }
	public String getAmenities() { return amenities; }
	public void setAmenities(String amenities) { this.amenities = amenities; }
	public String getImageUrl() { return imageUrl; }
	public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
	public Double getAverageRating() { return averageRating; }
	public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }
	public Integer getActiveLikeCount() { return activeLikeCount; }
	public void setActiveLikeCount(Integer activeLikeCount) { this.activeLikeCount = activeLikeCount; }
	public ActivityStatus getActivityStatus() { return activityStatus; }
	public void setActivityStatus(ActivityStatus activityStatus) { this.activityStatus = activityStatus; }
	public LocalDateTime getCreatedAt() { return createdAt; }
	public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
	public List<BarTable> getTables() { return tables; }
	public List<Event> getEvents() { return events; }
	public List<TimedLike> getLikes() { return likes; }
	public List<Review> getReviews() { return reviews; }
}
