package com.mitocode.profesional.infrastructure;

import com.mitocode.profesional.application.ProfesionalService;
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
import java.util.List;
import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/profesionales")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Profesionales", description = "Gestion de profesionales que atienden reservas")
public class ProfesionalResource {

    @Inject
    ProfesionalService service;

    @POST
    @Operation(summary = "Crear profesional", description = "Registra un nuevo profesional")
    @APIResponse(responseCode = "201", description = "Profesional creado")
    @APIResponse(responseCode = "400", description = "Datos de entrada invalidos")
    public Uni<Response> crear(@Valid ProfesionalRequest request) {
        return service.crear(request.nombres(), request.apellidos(), request.especialidad())
                .map(ProfesionalResponse::from)
                .map(response -> Response.created(URI.create("/profesionales/" + response.id()))
                        .entity(response)
                        .build());
    }

    @GET
    @Path("/{id}")
    @WithSession
    @Operation(summary = "Obtener profesional", description = "Busca un profesional por su identificador")
    @APIResponse(responseCode = "200", description = "Profesional encontrado")
    @APIResponse(responseCode = "404", description = "Profesional no encontrado")
    public Uni<ProfesionalResponse> obtener(@PathParam("id") UUID id) {
        return service.obtenerPorId(id).map(ProfesionalResponse::from);
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @WithSession
    @Operation(summary = "Actualizar profesional", description = "Actualiza los datos de un profesional existente")
    @APIResponse(responseCode = "200", description = "Profesional actualizado")
    @APIResponse(responseCode = "404", description = "Profesional no encontrado")
    public Uni<ProfesionalResponse> actualizar(@PathParam("id") UUID id, @Valid ProfesionalRequest request) {
        return service.actualizar(id, request.nombres(), request.apellidos(), request.especialidad())
                .map(ProfesionalResponse::from);
    }

    @DELETE
    @Path("/{id}")
    @WithSession
    @Operation(summary = "Eliminar profesional", description = "Elimina un profesional existente")
    @APIResponse(responseCode = "204", description = "Profesional eliminado")
    @APIResponse(responseCode = "404", description = "Profesional no encontrado")
    public Uni<Response> eliminar(@PathParam("id") UUID id) {
        return service.eliminar(id).map(ignored -> Response.noContent().build());
    }

    @GET
    @WithSession
    @Operation(summary = "Listar profesionales", description = "Lista los profesionales ordenados por cantidad de reservas activas")
    @APIResponse(responseCode = "200", description = "Listado de profesionales")
    public Uni<List<ProfesionalConReservasActivasResponse>> listar() {
        return service.listarOrdenadosPorReservasActivas()
                .map(lista -> lista.stream().map(ProfesionalConReservasActivasResponse::from).toList());
    }
}
