package app.db.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;

/** A hopper's star rating and written review of a bar. One per hopper per bar. Never expires. */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "hopper_id", "bar_id" }))
public class Review {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@ManyToOne(optional = false)
	private Hopper hopper;

	@NotNull
	@ManyToOne(optional = false)
	private Bar bar;

	@NotNull
	@Min(0)
	@Max(5)
	@Column(nullable = false)
	private Integer rating;

	@Size(max = 1000)
	@Column(length = 1000)
	private String comment;

	@PastOrPresent
	private LocalDate visitDate;

	@NotNull
	@Column(nullable = false)
	private LocalDateTime createdAt;

	@NotNull
	@Column(nullable = false)
	private LocalDateTime updatedAt;

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public Hopper getHopper() { return hopper; }
	public void setHopper(Hopper hopper) { this.hopper = hopper; }
	public Bar getBar() { return bar; }
	public void setBar(Bar bar) { this.bar = bar; }
	public Integer getRating() { return rating; }
	public void setRating(Integer rating) { this.rating = rating; }
	public String getComment() { return comment; }
	public void setComment(String comment) { this.comment = comment; }
	public LocalDate getVisitDate() { return visitDate; }
	public void setVisitDate(LocalDate visitDate) { this.visitDate = visitDate; }
	public LocalDateTime getCreatedAt() { return createdAt; }
	public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
	public LocalDateTime getUpdatedAt() { return updatedAt; }
	public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
