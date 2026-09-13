package com.mitocode.horario.infrastructure;

import com.mitocode.horario.domain.HorarioDisponible;
import com.mitocode.horario.domain.HorarioDisponibleRepository;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class HorarioDisponibleRepositoryImpl
        implements HorarioDisponibleRepository, PanacheRepositoryBase<HorarioDisponible, UUID> {

    @Override
    public Uni<HorarioDisponible> guardar(HorarioDisponible horario) {
        return Panache.withTransaction(() -> persist(horario)).replaceWith(horario);
    }

    @Override
    public Uni<List<HorarioDisponible>> buscarPorProfesionalYFecha(UUID profesionalId, LocalDate fecha) {
        return list("profesionalId = ?1 and fecha = ?2", profesionalId, fecha);
    }
}
