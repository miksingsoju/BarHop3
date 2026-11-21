package app.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class LoginRequest {
	@NotBlank @Email
	public String email;
	@NotBlank
	public String password;
}
