package com.mitocode.cliente.infrastructure;

import com.mitocode.cliente.application.ClienteService;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.UUID;

@Path("/clientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClienteResource {

    @Inject
    ClienteService service;

    @POST
    public Uni<Response> crear(@Valid ClienteRequest request) {
        return service.crear(request.nombres(), request.apellidos(), request.email(), request.telefono())
                .map(ClienteResponse::from)
                .map(response -> Response.created(URI.create("/clientes/" + response.id()))
                        .entity(response)
                        .build());
    }

    @GET
    @Path("/{id}")
    @WithSession
    public Uni<ClienteResponse> obtener(@PathParam("id") UUID id) {
        return service.obtenerPorId(id).map(ClienteResponse::from);
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @WithSession
    public Uni<ClienteResponse> actualizar(@PathParam("id") UUID id, @Valid ClienteRequest request) {
        return service.actualizar(id, request.nombres(), request.apellidos(), request.email(), request.telefono())
                .map(ClienteResponse::from);
    }

    @DELETE
    @Path("/{id}")
    @WithSession
    public Uni<Response> eliminar(@PathParam("id") UUID id) {
        return service.eliminar(id).map(ignored -> Response.noContent().build());
    }
}
