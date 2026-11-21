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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import app.components.HopperComponent;
import app.dto.HopperRequest;
import app.dto.HopperResponse;
import app.dto.HopperUpdateRequest;
import app.dto.LoginRequest;
import app.dto.MessageResponse;

@Component
@Path("/api/hoppers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HopperController {

	@Autowired
	private HopperComponent hopperComponent;

	@POST
	public Response register(@NotNull @Valid HopperRequest req) {
		return Response.status(Response.Status.CREATED).entity(hopperComponent.registerHopper(req)).build();
	}

	@POST
	@Path("/login")
	public HopperResponse login(@NotNull @Valid LoginRequest req) {
		return hopperComponent.login(req);
	}

	@GET
	public List<HopperResponse> getAll() {
		return hopperComponent.getAllHoppers();
	}

	@GET
	@Path("/{id}")
	public HopperResponse get(@PathParam("id") Long id) {
		return hopperComponent.getHopper(id);
	}

	@POST
	@Path("/{id}/update")
	public HopperResponse update(@PathParam("id") Long id, @NotNull @Valid HopperUpdateRequest req) {
		return hopperComponent.updateHopper(id, req);
	}

	@POST
	@Path("/{id}/delete")
	public MessageResponse delete(@PathParam("id") Long id) {
		return hopperComponent.deleteHopper(id);
	}
}
