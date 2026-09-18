package com.mitocode.reserva.infrastructure;

import com.mitocode.reserva.application.ReservaService;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/reservas")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Reservas", description = "Gestion de reservas de clientes con profesionales")
public class ReservaResource {

    @Inject
    ReservaService service;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @WithSession
    @Operation(summary = "Crear reserva", description = "Registra una nueva reserva en estado CREADA, validando disponibilidad y solapamiento")
    @APIResponse(responseCode = "201", description = "Reserva creada")
    @APIResponse(responseCode = "400", description = "Datos de entrada invalidos")
    @APIResponse(responseCode = "404", description = "Cliente o profesional no encontrado")
    @APIResponse(responseCode = "409", description = "La reserva se solapa con otra reserva activa")
    @APIResponse(responseCode = "422", description = "No existe un horario disponible que cubra el rango solicitado")
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
    @Operation(summary = "Cancelar reserva", description = "Transiciona una reserva de CREADA a CANCELADA")
    @APIResponse(responseCode = "200", description = "Reserva cancelada")
    @APIResponse(responseCode = "404", description = "Reserva no encontrada")
    @APIResponse(responseCode = "409", description = "La reserva no se encuentra en estado CREADA")
    public Uni<ReservaResponse> cancelar(@PathParam("id") UUID id) {
        return service.cancelar(id).map(ReservaResponse::from);
    }

    @POST
    @Path("/{id}/completar")
    @WithSession
    @Operation(summary = "Completar reserva", description = "Transiciona una reserva de CREADA a COMPLETADA")
    @APIResponse(responseCode = "200", description = "Reserva completada")
    @APIResponse(responseCode = "404", description = "Reserva no encontrada")
    @APIResponse(responseCode = "409", description = "La reserva no se encuentra en estado CREADA")
    public Uni<ReservaResponse> completar(@PathParam("id") UUID id) {
        return service.completar(id).map(ReservaResponse::from);
    }

    @GET
    @WithSession
    @Operation(summary = "Listar reservas agrupadas por fecha", description = "Devuelve todas las reservas agrupadas por fecha")
    @APIResponse(responseCode = "200", description = "Reservas agrupadas por fecha")
    public Uni<Map<LocalDate, List<ReservaResponse>>> listarAgrupadasPorFecha() {
        return service.agruparPorFecha()
                .map(porFecha -> porFecha.entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey,
                                entry -> entry.getValue().stream().map(ReservaResponse::from).toList())));
    }
}
