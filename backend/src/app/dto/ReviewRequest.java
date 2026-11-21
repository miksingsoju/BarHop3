package app.dto;

import java.time.LocalDate;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;

/** Used for create (barId required) and update (barId ignored). */
public class ReviewRequest {
	@NotNull
	public Long hopperId;
	public Long barId;
	@NotNull
	@Min(value = 0, message = "rating must be between 0 and 5.")
	@Max(value = 5, message = "rating must be between 0 and 5.")
	public Integer rating;
	@Size(max = 1000)
	public String comment;
	@PastOrPresent
	public LocalDate visitDate;
}
