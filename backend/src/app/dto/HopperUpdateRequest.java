package app.dto;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/** Fields left out (null) are not changed. */
public class HopperUpdateRequest {
	@Size(min = 1, max = 50)
	public String firstName;
	@Size(min = 1, max = 50)
	public String lastName;
	@Pattern(regexp = Patterns.PH_MOBILE, message = Patterns.PH_MOBILE_MESSAGE)
	public String phoneNumber;
}
