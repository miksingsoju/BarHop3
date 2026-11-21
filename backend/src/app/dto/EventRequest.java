package app.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class EventRequest {
	@NotNull
	public Long ownerId;
	@NotNull
	public Long barId;
	@NotBlank @Size(max = 100)
	public String title;
	@Size(max = 1000)
	public String description;
	@NotNull @Future
	public LocalDateTime startDateTime;
	@NotNull
	public LocalDateTime endDateTime;
	@DecimalMin("0.00")
	public BigDecimal coverCharge;
}
