package app.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import app.db.entity.Reservation;
import app.db.entity.ReservationStatus;

public class ReservationResponse {
	public Long id;
	public Long hopperId;
	public Long barId;
	public String barName;
	public Long tableId;
	public String tableLabel;
	public LocalDate reservationDate;
	@JsonFormat(pattern = Formats.TIME)
	public LocalTime startTime;
	@JsonFormat(pattern = Formats.TIME)
	public LocalTime endTime;
	public Integer guestCount;
	public String contactName;
	public String contactNumber;
	public ReservationStatus status;
	public Boolean agreedToPolicy;
	@JsonFormat(pattern = Formats.DATE_TIME)
	public LocalDateTime createdAt;

	public static ReservationResponse from(Reservation res) {
		ReservationResponse r = new ReservationResponse();
		r.id = res.getId();
		r.hopperId = res.getHopper().getId();
		r.barId = res.getBar().getId();
		r.barName = res.getBar().getName();
		r.tableId = res.getTable().getId();
		r.tableLabel = res.getTable().getLabel();
		r.reservationDate = res.getReservationDate();
		r.startTime = res.getStartTime();
		r.endTime = res.getEndTime();
		r.guestCount = res.getGuestCount();
		r.contactName = res.getContactName();
		r.contactNumber = res.getContactNumber();
		r.status = res.getStatus();
		r.agreedToPolicy = res.getAgreedToPolicy();
		r.createdAt = res.getCreatedAt();
		return r;
	}
}
