package com.mitocode.reserva.infrastructure;

import com.mitocode.reserva.application.ReservaService;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.UUID;

@Path("/reservas")
@Produces(MediaType.APPLICATION_JSON)
public class ReservaResource {

    @Inject
    ReservaService service;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @WithSession
    public Uni<Response> crear(@Valid ReservaRequest request) {
        return service.crear(request.clienteId(), request.profesionalId(), request.fecha(), request.horaInicio(),
                request.horaFin())
                .map(ReservaResponse::from)
                .map(response -> Response.created(URI.create("/reservas/" + response.id()))
                        .entity(response)
                        .build());
    }

    @POST
    @Path("/{id}/cancelar")
    @WithSession
    public Uni<ReservaResponse> cancelar(@PathParam("id") UUID id) {
        return service.cancelar(id).map(ReservaResponse::from);
    }
}
