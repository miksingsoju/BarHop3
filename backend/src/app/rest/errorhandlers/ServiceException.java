package app.rest.errorhandlers;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// DEFAULT EXCEPTION HANDLER: anything not handled by a more specific mapper.
// The stack trace goes to the console; the client gets the standard JSON error body.

@Provider
public class ServiceException implements ExceptionMapper<Throwable> {

	private static final Logger log = LoggerFactory.getLogger(ServiceException.class);

	@Override
	public Response toResponse(Throwable exception) {
		log.error("Unhandled error", exception);
		return ErrorResponses.build(500, "Something went wrong on the server: " + exception.getClass().getSimpleName());
	}
}
