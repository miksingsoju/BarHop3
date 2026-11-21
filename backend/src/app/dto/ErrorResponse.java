package app.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import app.Clock;

/** The standard error body. errors is only present for validation failures. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
	public int status;
	public String error;
	public String message;
	@JsonFormat(pattern = Formats.DATE_TIME)
	public LocalDateTime timestamp;
	public List<FieldError> errors;

	public ErrorResponse(int status, String error, String message, List<FieldError> errors) {
		this.status = status;
		this.error = error;
		this.message = message;
		this.timestamp = Clock.now();
		this.errors = errors;
	}

	public static class FieldError {
		public String field;
		public String message;

		public FieldError(String field, String message) {
			this.field = field;
			this.message = message;
		}
	}
}
