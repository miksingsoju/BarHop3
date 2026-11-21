package app.rest.controllers;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
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

import app.components.BarComponent;
import app.components.LikeComponent;
import app.dto.ActivityResponse;
import app.dto.BarDetail;
import app.dto.BarRequest;
import app.dto.BarSummary;
import app.dto.MessageResponse;

@Component
@Path("/api/bars")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BarController {

	@Autowired
	private BarComponent barComponent;

	@Autowired
	private LikeComponent likeComponent;

	/** Home page feed. */
	@GET
	public List<BarSummary> search(@QueryParam("city") String city, @QueryParam("search") String search,
			@QueryParam("status") String status, @QueryParam("sort") String sort) {
		return barComponent.searchBars(city, search, status, sort);
	}

	@GET
	@Path("/{id}")
	public BarDetail get(@PathParam("id") Long id) {
		return barComponent.getBar(id);
	}

	@GET
	@Path("/owner/{ownerId}")
	public List<BarSummary> getByOwner(@PathParam("ownerId") Long ownerId) {
		return barComponent.getBarsByOwner(ownerId);
	}

	@GET
	@Path("/{id}/activity")
	public ActivityResponse activity(@PathParam("id") Long id) {
		return likeComponent.getActivity(id);
	}

	@POST
	public Response create(@NotNull @Valid BarRequest req) {
		return Response.status(Response.Status.CREATED).entity(barComponent.createBar(req)).build();
	}

	@POST
	@Path("/{id}/update")
	public BarDetail update(@PathParam("id") Long id, @NotNull @Valid BarRequest req) {
		return barComponent.updateBar(id, req);
	}

	@POST
	@Path("/{id}/delete")
	public MessageResponse delete(@PathParam("id") Long id, @QueryParam("ownerId") Long ownerId) {
		return barComponent.deleteBar(id, Params.require(ownerId, "ownerId"));
	}
}
