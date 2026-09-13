package com.mitocode.horario.infrastructure;

import com.mitocode.horario.domain.HorarioDisponible;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record HorarioDisponibleResponse(
        UUID id,
        UUID profesionalId,
        LocalDate fecha,
        LocalTime horaInicio,
        LocalTime horaFin,
        boolean estado) {

    public static HorarioDisponibleResponse from(HorarioDisponible horario) {
        return new HorarioDisponibleResponse(
                horario.getId(),
                horario.getProfesionalId(),
                horario.getFecha(),
                horario.getRango().inicio(),
                horario.getRango().fin(),
                horario.isEstado());
    }
}
