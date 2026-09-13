package com.mitocode.reserva.infrastructure;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record ReservaRequest(
        @NotNull UUID clienteId,
        @NotNull UUID profesionalId,
        @NotNull LocalDate fecha,
        @NotNull LocalTime horaInicio,
        @NotNull LocalTime horaFin) {
}
