package app.db.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/** A hopper's "I'm here and it's lively" signal for a bar. Lasts 90 minutes. */
@Entity
@Table(indexes = @Index(name = "idx_timed_like_expires_at", columnList = "expiresAt"))
public class TimedLike {

	public static final long DURATION_MINUTES = 90;

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
	@Column(nullable = false)
	private LocalDateTime likedAt;

	/** Always likedAt + 90 minutes. */
	@NotNull
	@Column(nullable = false)
	private LocalDateTime expiresAt;

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public Hopper getHopper() { return hopper; }
	public void setHopper(Hopper hopper) { this.hopper = hopper; }
	public Bar getBar() { return bar; }
	public void setBar(Bar bar) { this.bar = bar; }
	public LocalDateTime getLikedAt() { return likedAt; }
	public void setLikedAt(LocalDateTime likedAt) { this.likedAt = likedAt; }
	public LocalDateTime getExpiresAt() { return expiresAt; }
	public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
}
