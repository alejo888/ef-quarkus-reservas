package com.mitocode.horario.infrastructure;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record HorarioDisponibleRequest(
        @NotNull UUID profesionalId,
        @NotNull LocalDate fecha,
        @NotNull LocalTime horaInicio,
        @NotNull LocalTime horaFin) {
}
