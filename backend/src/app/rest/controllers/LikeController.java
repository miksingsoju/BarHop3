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

import app.components.LikeComponent;
import app.dto.LikeRequest;
import app.dto.LikeResponse;
import app.dto.MessageResponse;

@Component
@Path("/api/likes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LikeController {

	@Autowired
	private LikeComponent likeComponent;

	/** Timed like: counts toward the bar's activity for 90 minutes. */
	@POST
	public Response like(@NotNull @Valid LikeRequest req) {
		return Response.status(Response.Status.CREATED).entity(likeComponent.likeBar(req)).build();
	}

	@GET
	public List<LikeResponse> active(@QueryParam("barId") Long barId, @QueryParam("hopperId") Long hopperId) {
		return likeComponent.getActiveLikes(barId, hopperId);
	}

	@POST
	@Path("/{id}/delete")
	public MessageResponse remove(@PathParam("id") Long id, @QueryParam("hopperId") Long hopperId) {
		return likeComponent.removeLike(id, Params.require(hopperId, "hopperId"));
	}
}
