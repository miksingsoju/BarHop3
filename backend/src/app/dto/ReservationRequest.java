package app.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ReservationRequest {
	@NotNull
	public Long hopperId;
	@NotNull
	public Long tableId;
	@NotNull
	public LocalDate reservationDate;
	@NotNull @JsonFormat(pattern = Formats.TIME)
	public LocalTime startTime;
	@NotNull @JsonFormat(pattern = Formats.TIME)
	public LocalTime endTime;
	@NotNull @Min(1)
	public Integer guestCount;
	@NotBlank @Size(max = 100)
	public String contactName;
	@NotBlank @Pattern(regexp = Patterns.PH_MOBILE, message = Patterns.PH_MOBILE_MESSAGE)
	public String contactNumber;
	/** Must be true; checked in ReservationComponent for a friendlier message. */
	@NotNull
	public Boolean agreedToPolicy;
}
