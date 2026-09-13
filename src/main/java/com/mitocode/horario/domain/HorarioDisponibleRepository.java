package com.mitocode.horario.domain;

import io.smallrye.mutiny.Uni;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Repository port for the HorarioDisponible aggregate. Implemented in the
 * infrastructure layer with Hibernate Reactive Panache.
 */
public interface HorarioDisponibleRepository {

    Uni<HorarioDisponible> guardar(HorarioDisponible horario);

    Uni<List<HorarioDisponible>> buscarPorProfesionalYFecha(UUID profesionalId, LocalDate fecha);
}
