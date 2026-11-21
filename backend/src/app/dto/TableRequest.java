package app.dto;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import app.db.entity.TableType;

/** Used for create (barId required) and update (barId ignored, active optional). */
public class TableRequest {
	@NotNull
	public Long ownerId;
	public Long barId;
	@NotBlank @Size(max = 50)
	public String label;
	@NotNull
	public TableType tableType;
	@NotNull @Min(1) @Max(30)
	public Integer capacity;
	@NotNull @DecimalMin("0.00") @Digits(integer = 7, fraction = 2)
	public BigDecimal rate;
	public Boolean active;
}
