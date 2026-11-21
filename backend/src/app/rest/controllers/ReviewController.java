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

import app.components.ReviewComponent;
import app.dto.MessageResponse;
import app.dto.ReviewRequest;
import app.dto.ReviewResponse;

@Component
@Path("/api/reviews")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReviewController {

	@Autowired
	private ReviewComponent reviewComponent;

	@GET
	public List<ReviewResponse> list(@QueryParam("barId") Long barId, @QueryParam("hopperId") Long hopperId) {
		return reviewComponent.getReviews(barId, hopperId);
	}

	@GET
	@Path("/{id}")
	public ReviewResponse get(@PathParam("id") Long id) {
		return reviewComponent.getReview(id);
	}

	@POST
	public Response create(@NotNull @Valid ReviewRequest req) {
		return Response.status(Response.Status.CREATED).entity(reviewComponent.createReview(req)).build();
	}

	@POST
	@Path("/{id}/update")
	public ReviewResponse update(@PathParam("id") Long id, @NotNull @Valid ReviewRequest req) {
		return reviewComponent.updateReview(id, req);
	}

	@POST
	@Path("/{id}/delete")
	public MessageResponse delete(@PathParam("id") Long id, @QueryParam("hopperId") Long hopperId) {
		return reviewComponent.deleteReview(id, Params.require(hopperId, "hopperId"));
	}
}
