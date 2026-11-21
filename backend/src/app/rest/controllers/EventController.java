package app.rest.controllers;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import app.components.EventComponent;
import app.dto.EventRequest;
import app.dto.EventResponse;
import app.dto.MessageResponse;

@Component
@Path("/api/events")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EventController {

	@Autowired
	private EventComponent eventComponent;

	@GET
	public List<EventResponse> list(@QueryParam("barId") Long barId,
			@QueryParam("upcomingOnly") @DefaultValue("true") boolean upcomingOnly) {
		return eventComponent.getEvents(barId, upcomingOnly);
	}

	@GET
	@Path("/{id}")
	public EventResponse get(@PathParam("id") Long id) {
		return eventComponent.getEvent(id);
	}

	@POST
	public Response create(@NotNull @Valid EventRequest req) {
		return Response.status(Response.Status.CREATED).entity(eventComponent.createEvent(req)).build();
	}

	@POST
	@Path("/{id}/delete")
	public MessageResponse delete(@PathParam("id") Long id, @QueryParam("ownerId") Long ownerId) {
		return eventComponent.deleteEvent(id, Params.require(ownerId, "ownerId"));
	}
}
