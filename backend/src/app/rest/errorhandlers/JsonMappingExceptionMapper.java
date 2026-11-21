package app.rest.errorhandlers;

import java.util.Collections;

import javax.annotation.Priority;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import app.dto.ErrorResponse.FieldError;

/** A JSON value of the wrong type or format (e.g. "startTime": "9pm") -> 400 with the field. */
@Provider
@Priority(1)
public class JsonMappingExceptionMapper implements ExceptionMapper<JsonMappingException> {

	@Override
	public Response toResponse(JsonMappingException e) {
		String field = "body";
		if (!e.getPath().isEmpty()) {
			String name = e.getPath().get(e.getPath().size() - 1).getFieldName();
			if (name != null) {
				field = name;
			}
		}
		String message = "has an invalid value";
		if (e instanceof InvalidFormatException) {
			InvalidFormatException ife = (InvalidFormatException) e;
			Class<?> target = ife.getTargetType();
			if (target != null && target.isEnum()) {
				StringBuilder allowed = new StringBuilder();
				for (Object c : target.getEnumConstants()) {
					if (allowed.length() > 0) allowed.append(", ");
					allowed.append(c);
				}
				message = "invalid value '" + ife.getValue() + "'. Allowed values: " + allowed;
			} else if (target != null && target.getSimpleName().equals("LocalTime")) {
				message = "invalid time '" + ife.getValue() + "'. Use HH:mm";
			}
		}
		if (e.getCause() != null && e.getCause().getClass().getSimpleName().equals("DateTimeParseException")) {
			message = "invalid date/time format";
		}
		return ErrorResponses.build(400, "Validation failed", Collections.singletonList(new FieldError(field, message)));
	}
}
