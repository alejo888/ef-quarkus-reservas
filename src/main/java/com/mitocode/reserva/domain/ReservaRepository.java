package com.mitocode.reserva.domain;

import io.smallrye.mutiny.Uni;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Repository port for the Reserva aggregate. Implemented in the
 * infrastructure layer with Hibernate Reactive Panache.
 */
public interface ReservaRepository {

    Uni<Reserva> guardar(Reserva reserva);

    /** Persists changes made to an already-existing (possibly detached) Reserva. */
    Uni<Reserva> actualizar(Reserva reserva);

    Uni<List<Reserva>> buscarPorProfesionalYFecha(UUID profesionalId, LocalDate fecha);

    /** Emits {@code null} when no reserva exists with the given id. */
    Uni<Reserva> buscarPorId(UUID id);

    Uni<List<Reserva>> buscarTodas();
}
