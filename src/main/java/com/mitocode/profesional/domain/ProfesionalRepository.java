package com.mitocode.profesional.domain;

import io.smallrye.mutiny.Uni;
import java.util.UUID;

/**
 * Repository port for the Profesional aggregate. Implemented in the
 * infrastructure layer with Hibernate Reactive Panache.
 */
public interface ProfesionalRepository {

    Uni<Profesional> guardar(Profesional profesional);

    /** Emits {@code null} when no profesional exists with the given id. */
    Uni<Profesional> buscarPorId(UUID id);
}
