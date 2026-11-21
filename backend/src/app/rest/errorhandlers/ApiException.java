package app.rest.errorhandlers;

import java.util.Collections;
import java.util.List;

import app.dto.ErrorResponse.FieldError;

/** Thrown by components; turned into the standard JSON error body by ApiExceptionMapper. */
public class ApiException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final int status;
	private final List<FieldError> errors;

	public ApiException(int status, String message) {
		this(status, message, null);
	}

	private ApiException(int status, String message, List<FieldError> errors) {
		super(message);
		this.status = status;
		this.errors = errors;
	}

	public int getStatus() { return status; }
	public List<FieldError> getErrors() { return errors; }

	public static ApiException badRequest(String message) { return new ApiException(400, message); }
	public static ApiException unauthorized(String message) { return new ApiException(401, message); }
	public static ApiException forbidden(String message) { return new ApiException(403, message); }
	public static ApiException notFound(String message) { return new ApiException(404, message); }
	public static ApiException conflict(String message) { return new ApiException(409, message); }

	/** A 400 in the validation format, for a single field checked in code. */
	public static ApiException fieldError(String field, String message) {
		return new ApiException(400, "Validation failed",
				Collections.singletonList(new FieldError(field, message)));
	}
}
