package app.db.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/** A Bar Hopper: a student or customer who browses, likes, reviews and reserves. */
@Entity
public class Hopper {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Size(max = 50)
	@Column(nullable = false, length = 50)
	private String firstName;

	@NotBlank
	@Size(max = 50)
	@Column(nullable = false, length = 50)
	private String lastName;

	@NotBlank
	@Email
	@Size(max = 100)
	@Column(nullable = false, unique = true, length = 100)
	private String email;

	@NotNull
	@Column(nullable = false)
	private String passwordHash;

	@NotBlank
	@Pattern(regexp = "^\\+639\\d{9}$")
	@Column(nullable = false, length = 13)
	private String phoneNumber;

	@NotNull
	@Column(nullable = false)
	private LocalDateTime createdAt;

	// Deletion of likes, reviews and reservations is done explicitly in HopperComponent
	// so bar ratings/activity can be recalculated and owners notified first.
	@OneToMany(mappedBy = "hopper")
	private List<TimedLike> likes = new ArrayList<>();

	@OneToMany(mappedBy = "hopper")
	private List<Review> reviews = new ArrayList<>();

	@OneToMany(mappedBy = "hopper")
	private List<Reservation> reservations = new ArrayList<>();

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getFirstName() { return firstName; }
	public void setFirstName(String firstName) { this.firstName = firstName; }
	public String getLastName() { return lastName; }
	public void setLastName(String lastName) { this.lastName = lastName; }
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	public String getPasswordHash() { return passwordHash; }
	public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
	public String getPhoneNumber() { return phoneNumber; }
	public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
	public LocalDateTime getCreatedAt() { return createdAt; }
	public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
	public List<TimedLike> getLikes() { return likes; }
	public List<Review> getReviews() { return reviews; }
	public List<Reservation> getReservations() { return reservations; }

	public String getFullName() { return firstName + " " + lastName; }
}
