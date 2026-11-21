package app.rest.errorhandlers;

import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import app.dto.ErrorResponse;
import app.dto.ErrorResponse.FieldError;

final class ErrorResponses {

	private ErrorResponses() {
	}

	static Response build(int status, String message, List<FieldError> errors) {
		Response.StatusType type = Response.Status.fromStatusCode(status);
		String reason = type != null ? type.getReasonPhrase() : "Error";
		return Response.status(status)
				.type(MediaType.APPLICATION_JSON)
				.entity(new ErrorResponse(status, reason, message, errors))
				.build();
	}

	static Response build(int status, String message) {
		return build(status, message, null);
	}
}
