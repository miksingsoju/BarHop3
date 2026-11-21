package app.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import app.db.entity.Owner;

public class OwnerResponse {
	public Long id;
	public String firstName;
	public String lastName;
	public String email;
	public String phoneNumber;
	public String businessName;
	public long barCount;
	@JsonFormat(pattern = Formats.DATE_TIME)
	public LocalDateTime createdAt;

	public static OwnerResponse from(Owner o, long barCount) {
		OwnerResponse r = new OwnerResponse();
		r.id = o.getId();
		r.firstName = o.getFirstName();
		r.lastName = o.getLastName();
		r.email = o.getEmail();
		r.phoneNumber = o.getPhoneNumber();
		r.businessName = o.getBusinessName();
		r.barCount = barCount;
		r.createdAt = o.getCreatedAt();
		return r;
	}
}
