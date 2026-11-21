package app.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class HopperRequest {
	@NotBlank @Size(max = 50)
	public String firstName;
	@NotBlank @Size(max = 50)
	public String lastName;
	@NotBlank @Email @Size(max = 100)
	public String email;
	@NotBlank @Size(min = 8, max = 72)
	public String password;
	@NotBlank @Pattern(regexp = Patterns.PH_MOBILE, message = Patterns.PH_MOBILE_MESSAGE)
	public String phoneNumber;
}
