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
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/clientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Clientes", description = "Gestion de clientes que solicitan reservas")
public class ClienteResource {

    @Inject
    ClienteService service;

    @POST
    @Operation(summary = "Crear cliente", description = "Registra un nuevo cliente")
    @APIResponse(responseCode = "201", description = "Cliente creado")
    @APIResponse(responseCode = "400", description = "Datos de entrada invalidos")
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
    @Operation(summary = "Obtener cliente", description = "Busca un cliente por su identificador")
    @APIResponse(responseCode = "200", description = "Cliente encontrado")
    @APIResponse(responseCode = "404", description = "Cliente no encontrado")
    public Uni<ClienteResponse> obtener(@PathParam("id") UUID id) {
        return service.obtenerPorId(id).map(ClienteResponse::from);
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @WithSession
    @Operation(summary = "Actualizar cliente", description = "Actualiza los datos de un cliente existente")
    @APIResponse(responseCode = "200", description = "Cliente actualizado")
    @APIResponse(responseCode = "404", description = "Cliente no encontrado")
    public Uni<ClienteResponse> actualizar(@PathParam("id") UUID id, @Valid ClienteRequest request) {
        return service.actualizar(id, request.nombres(), request.apellidos(), request.email(), request.telefono())
                .map(ClienteResponse::from);
    }

    @DELETE
    @Path("/{id}")
    @WithSession
    @Operation(summary = "Eliminar cliente", description = "Elimina un cliente existente")
    @APIResponse(responseCode = "204", description = "Cliente eliminado")
    @APIResponse(responseCode = "404", description = "Cliente no encontrado")
    public Uni<Response> eliminar(@PathParam("id") UUID id) {
        return service.eliminar(id).map(ignored -> Response.noContent().build());
    }
}
