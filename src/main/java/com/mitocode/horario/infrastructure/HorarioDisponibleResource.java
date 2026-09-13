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

@Path("/horarios-disponibles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HorarioDisponibleResource {

    @Inject
    HorarioDisponibleService service;

    @POST
    @WithSession
    public Uni<Response> crear(@Valid HorarioDisponibleRequest request) {
        return service.crear(request.profesionalId(), request.fecha(), request.horaInicio(), request.horaFin())
                .map(HorarioDisponibleResponse::from)
                .map(response -> Response.created(URI.create("/horarios-disponibles/" + response.id()))
                        .entity(response)
                        .build());
    }
}
