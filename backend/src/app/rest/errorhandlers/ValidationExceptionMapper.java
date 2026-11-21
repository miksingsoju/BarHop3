package app.rest.errorhandlers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import app.dto.ErrorResponse.FieldError;

/** Bean Validation failures on request bodies -> 400 with an errors array. */
@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

	@Override
	public Response toResponse(ConstraintViolationException e) {
		List<FieldError> errors = new ArrayList<>();
		for (ConstraintViolation<?> v : e.getConstraintViolations()) {
			errors.add(new FieldError(fieldName(v.getPropertyPath()), v.getMessage()));
		}
		errors.sort(Comparator.comparing((FieldError f) -> f.field));
		return ErrorResponses.build(400, "Validation failed", errors);
	}

	/** "register.arg0.email" -> "email"; a missing body ("register.arg0") -> "body". */
	private static String fieldName(Path path) {
		String last = null;
		for (Path.Node node : path) {
			last = node.getName();
		}
		if (last == null || last.matches("arg\\d+")) {
			return "body";
		}
		return last;
	}
}
