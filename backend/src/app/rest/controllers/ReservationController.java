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

import app.components.ReservationComponent;
import app.dto.MessageResponse;
import app.dto.ReservationRequest;
import app.dto.ReservationResponse;
import app.dto.StatusUpdateRequest;

@Component
@Path("/api/reservations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReservationController {

	@Autowired
	private ReservationComponent reservationComponent;

	/** A hopper's bookings (hopperId) or a bar's bookings (barId, optional date). */
	@GET
	public List<ReservationResponse> list(@QueryParam("hopperId") Long hopperId, @QueryParam("barId") Long barId,
			@QueryParam("date") String date) {
		return reservationComponent.getReservations(hopperId, barId, Params.date(date, "date"));
	}

	@GET
	@Path("/{id}")
	public ReservationResponse get(@PathParam("id") Long id) {
		return reservationComponent.getReservation(id);
	}

	@POST
	public Response create(@NotNull @Valid ReservationRequest req) {
		return Response.status(Response.Status.CREATED).entity(reservationComponent.createReservation(req)).build();
	}

	/** Owner confirms, cancels, or marks as NO_SHOW / COMPLETED. */
	@POST
	@Path("/{id}/status")
	public ReservationResponse updateStatus(@PathParam("id") Long id, @NotNull @Valid StatusUpdateRequest req) {
		return reservationComponent.updateStatus(id, req);
	}

	/** Hopper cancels their own booking. */
	@POST
	@Path("/{id}/cancel")
	public ReservationResponse cancel(@PathParam("id") Long id, @QueryParam("hopperId") Long hopperId) {
		return reservationComponent.cancelReservation(id, Params.require(hopperId, "hopperId"));
	}

	@POST
	@Path("/{id}/delete")
	public MessageResponse delete(@PathParam("id") Long id, @QueryParam("ownerId") Long ownerId) {
		return reservationComponent.deleteReservation(id, Params.require(ownerId, "ownerId"));
	}
}
