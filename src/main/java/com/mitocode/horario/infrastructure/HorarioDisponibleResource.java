package com.mitocode.horario.infrastructure;

import com.mitocode.horario.application.HorarioDisponibleService;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/horarios-disponibles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Horarios disponibles", description = "Gestion de la disponibilidad horaria de los profesionales")
public class HorarioDisponibleResource {

    @Inject
    HorarioDisponibleService service;

    @POST
    @WithSession
    @Operation(summary = "Crear horario disponible", description = "Registra un bloque de disponibilidad horaria para un profesional")
    @APIResponse(responseCode = "201", description = "Horario disponible creado")
    @APIResponse(responseCode = "400", description = "Datos de entrada invalidos")
    @APIResponse(responseCode = "404", description = "Profesional no encontrado")
    public Uni<Response> crear(@Valid HorarioDisponibleRequest request) {
        return service.crear(request.profesionalId(), request.fecha(), request.horaInicio(), request.horaFin())
                .map(HorarioDisponibleResponse::from)
                .map(response -> Response.created(URI.create("/horarios-disponibles/" + response.id()))
                        .entity(response)
                        .build());
    }
}
