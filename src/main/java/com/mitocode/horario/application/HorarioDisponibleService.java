package com.mitocode.horario.application;

import com.mitocode.horario.domain.HorarioDisponible;
import com.mitocode.horario.domain.HorarioDisponibleRepository;
import com.mitocode.horario.domain.ValidadorSolapamientoHorario;
import com.mitocode.shared.domain.RangoHorario;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@ApplicationScoped
public class HorarioDisponibleService {

    @Inject
    HorarioDisponibleRepository repository;

    public Uni<HorarioDisponible> crear(UUID profesionalId, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        HorarioDisponible nuevo = HorarioDisponible.crear(profesionalId, fecha, new RangoHorario(horaInicio, horaFin));

        return repository.buscarPorProfesionalYFecha(profesionalId, fecha)
                .invoke(existentes -> ValidadorSolapamientoHorario.validar(nuevo, existentes))
                .chain(existentes -> repository.guardar(nuevo));
    }
}
