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

import app.components.TableComponent;
import app.dto.MessageResponse;
import app.dto.TableRequest;
import app.dto.TableResponse;
import app.rest.errorhandlers.ApiException;

@Component
@Path("/api/tables")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TableController {

	@Autowired
	private TableComponent tableComponent;

	@GET
	public List<TableResponse> list(@QueryParam("barId") Long barId) {
		return tableComponent.getTablesForBar(Params.require(barId, "barId"));
	}

	/** Free tables for a date, time window and party size (reservation calendar). */
	@GET
	@Path("/available")
	public List<TableResponse> available(@QueryParam("barId") Long barId, @QueryParam("date") String date,
			@QueryParam("startTime") String startTime, @QueryParam("endTime") String endTime,
			@QueryParam("guests") Integer guests) {
		Params.require(barId, "barId");
		Params.require(date, "date");
		Params.require(startTime, "startTime");
		Params.require(endTime, "endTime");
		Params.require(guests, "guests");
		if (guests < 1) {
			throw ApiException.badRequest("Query parameter 'guests' must be at least 1.");
		}
		return tableComponent.getAvailableTables(barId, Params.date(date, "date"), Params.time(startTime, "startTime"),
				Params.time(endTime, "endTime"), guests);
	}

	@GET
	@Path("/{id}")
	public TableResponse get(@PathParam("id") Long id) {
		return tableComponent.getTable(id);
	}

	@POST
	public Response create(@NotNull @Valid TableRequest req) {
		return Response.status(Response.Status.CREATED).entity(tableComponent.createTable(req)).build();
	}

	@POST
	@Path("/{id}/update")
	public TableResponse update(@PathParam("id") Long id, @NotNull @Valid TableRequest req) {
		return tableComponent.updateTable(id, req);
	}

	@POST
	@Path("/{id}/delete")
	public MessageResponse delete(@PathParam("id") Long id, @QueryParam("ownerId") Long ownerId) {
		return tableComponent.deleteTable(id, Params.require(ownerId, "ownerId"));
	}
}
