package app.rest.errorhandlers;

import javax.ws.rs.NotAllowedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.server.ParamException;

/** Jersey's own errors (unknown path, wrong method, bad query param type) in the standard format. */
@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

	@Override
	public Response toResponse(WebApplicationException e) {
		if (e instanceof ParamException) {
			ParamException pe = (ParamException) e;
			return ErrorResponses.build(400, "Parameter '" + pe.getParameterName() + "' has an invalid value.");
		}
		if (e instanceof NotFoundException) {
			return ErrorResponses.build(404, "No endpoint matches this URL.");
		}
		if (e instanceof NotAllowedException) {
			return ErrorResponses.build(405, "This HTTP method is not supported here. Use GET to read and POST to submit.");
		}
		int status = e.getResponse() != null ? e.getResponse().getStatus() : 500;
		return ErrorResponses.build(status, e.getMessage());
	}
}
