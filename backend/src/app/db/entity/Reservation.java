package app.db.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * A hopper's booking of a table for a date and time window.
 * reservationDate is the calendar date on which the booking starts.
 * endTime may be earlier than startTime for a slot that runs past midnight.
 */
@Entity
public class Reservation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@ManyToOne(optional = false)
	private Hopper hopper;

	/** The bar is reached through the table. */
	@NotNull
	@ManyToOne(optional = false)
	private BarTable table;

	@NotNull
	@Column(nullable = false)
	private LocalDate reservationDate;

	@NotNull
	@Column(nullable = false)
	private LocalTime startTime;

	@NotNull
	@Column(nullable = false)
	private LocalTime endTime;

	@NotNull
	@Min(1)
	@Column(nullable = false)
	private Integer guestCount;

	@NotBlank
	@Size(max = 100)
	@Column(nullable = false, length = 100)
	private String contactName;

	@NotBlank
	@Pattern(regexp = "^\\+639\\d{9}$")
	@Column(nullable = false, length = 13)
	private String contactNumber;

	@NotNull
	@AssertTrue
	@Column(nullable = false)
	private Boolean agreedToPolicy;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	private ReservationStatus status = ReservationStatus.PENDING;

	@Column(nullable = false)
	private Boolean reminderSent = Boolean.FALSE;

	@NotNull
	@Column(nullable = false)
	private LocalDateTime createdAt;

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public Hopper getHopper() { return hopper; }
	public void setHopper(Hopper hopper) { this.hopper = hopper; }
	public BarTable getTable() { return table; }
	public void setTable(BarTable table) { this.table = table; }
	public LocalDate getReservationDate() { return reservationDate; }
	public void setReservationDate(LocalDate reservationDate) { this.reservationDate = reservationDate; }
	public LocalTime getStartTime() { return startTime; }
	public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
	public LocalTime getEndTime() { return endTime; }
	public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
	public Integer getGuestCount() { return guestCount; }
	public void setGuestCount(Integer guestCount) { this.guestCount = guestCount; }
	public String getContactName() { return contactName; }
	public void setContactName(String contactName) { this.contactName = contactName; }
	public String getContactNumber() { return contactNumber; }
	public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
	public Boolean getAgreedToPolicy() { return agreedToPolicy; }
	public void setAgreedToPolicy(Boolean agreedToPolicy) { this.agreedToPolicy = agreedToPolicy; }
	public ReservationStatus getStatus() { return status; }
	public void setStatus(ReservationStatus status) { this.status = status; }
	public Boolean getReminderSent() { return reminderSent; }
	public void setReminderSent(Boolean reminderSent) { this.reminderSent = reminderSent; }
	public LocalDateTime getCreatedAt() { return createdAt; }
	public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

	public Bar getBar() { return table.getBar(); }

	/** Actual start as a date-time. */
	public LocalDateTime getStartDateTime() {
		return reservationDate.atTime(startTime);
	}

	/** Actual end as a date-time (next day when the slot runs past midnight). */
	public LocalDateTime getEndDateTime() {
		LocalDateTime end = reservationDate.atTime(endTime);
		return endTime.isAfter(startTime) ? end : end.plusDays(1);
	}
}
