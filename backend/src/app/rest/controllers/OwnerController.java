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

import app.components.OwnerComponent;
import app.dto.LoginRequest;
import app.dto.MessageResponse;
import app.dto.OwnerRequest;
import app.dto.OwnerResponse;
import app.dto.OwnerUpdateRequest;

@Component
@Path("/api/owners")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OwnerController {

	@Autowired
	private OwnerComponent ownerComponent;

	@POST
	public Response register(@NotNull @Valid OwnerRequest req) {
		return Response.status(Response.Status.CREATED).entity(ownerComponent.registerOwner(req)).build();
	}

	@POST
	@Path("/login")
	public OwnerResponse login(@NotNull @Valid LoginRequest req) {
		return ownerComponent.login(req);
	}

	@GET
	public List<OwnerResponse> getAll() {
		return ownerComponent.getAllOwners();
	}

	@GET
	@Path("/{id}")
	public OwnerResponse get(@PathParam("id") Long id) {
		return ownerComponent.getOwner(id);
	}

	@POST
	@Path("/{id}/update")
	public OwnerResponse update(@PathParam("id") Long id, @NotNull @Valid OwnerUpdateRequest req) {
		return ownerComponent.updateOwner(id, req);
	}

	@POST
	@Path("/{id}/delete")
	public MessageResponse delete(@PathParam("id") Long id) {
		return ownerComponent.deleteOwner(id);
	}
}
