package app.dto;

import java.time.LocalTime;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;

/** Used for both create and update. */
public class BarRequest {
	@NotNull
	public Long ownerId;
	@NotBlank @Size(max = 100)
	public String name;
	@Size(max = 1000)
	public String description;
	@NotBlank @Size(max = 200)
	public String address;
	@NotBlank
	public String city;
	@NotNull @JsonFormat(pattern = Formats.TIME)
	public LocalTime openingTime;
	@NotNull @JsonFormat(pattern = Formats.TIME)
	public LocalTime closingTime;
	@NotNull @Min(1) @Max(5000)
	public Integer capacity;
	@Size(max = 500)
	public String amenities;
	@Size(max = 300)
	public String imageUrl;
}
