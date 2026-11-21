package app.db.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/** A scheduled event at a bar (live band, theme night, promo). */
@Entity
public class Event {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@ManyToOne(optional = false)
	private Bar bar;

	@NotBlank
	@Size(max = 100)
	@Column(nullable = false, length = 100)
	private String title;

	@Size(max = 1000)
	@Column(length = 1000)
	private String description;

	/** Must be in the future when created (checked on EventRequest). */
	@NotNull
	@Column(nullable = false)
	private LocalDateTime startDateTime;

	/** Must be after startDateTime (checked in EventComponent). */
	@NotNull
	@Column(nullable = false)
	private LocalDateTime endDateTime;

	/** Null means free entry. */
	@DecimalMin("0.00")
	@Column(precision = 9, scale = 2)
	private BigDecimal coverCharge;

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public Bar getBar() { return bar; }
	public void setBar(Bar bar) { this.bar = bar; }
	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	public LocalDateTime getStartDateTime() { return startDateTime; }
	public void setStartDateTime(LocalDateTime startDateTime) { this.startDateTime = startDateTime; }
	public LocalDateTime getEndDateTime() { return endDateTime; }
	public void setEndDateTime(LocalDateTime endDateTime) { this.endDateTime = endDateTime; }
	public BigDecimal getCoverCharge() { return coverCharge; }
	public void setCoverCharge(BigDecimal coverCharge) { this.coverCharge = coverCharge; }
}
