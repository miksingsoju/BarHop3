package app.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class StatusUpdateRequest {
	@NotNull
	public Long ownerId;
	/** CONFIRMED, CANCELLED, COMPLETED or NO_SHOW. */
	@NotBlank
	public String status;
}
