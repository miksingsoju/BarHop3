package app.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import app.db.entity.Hopper;

public class HopperResponse {
	public Long id;
	public String firstName;
	public String lastName;
	public String email;
	public String phoneNumber;
	@JsonFormat(pattern = Formats.DATE_TIME)
	public LocalDateTime createdAt;

	public static HopperResponse from(Hopper h) {
		HopperResponse r = new HopperResponse();
		r.id = h.getId();
		r.firstName = h.getFirstName();
		r.lastName = h.getLastName();
		r.email = h.getEmail();
		r.phoneNumber = h.getPhoneNumber();
		r.createdAt = h.getCreatedAt();
		return r;
	}
}
