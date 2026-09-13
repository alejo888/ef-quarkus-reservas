package com.mitocode.reserva.infrastructure;

import com.mitocode.reserva.domain.EstadoReserva;
import com.mitocode.reserva.domain.Reserva;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record ReservaResponse(
        UUID id,
        UUID clienteId,
        UUID profesionalId,
        LocalDate fecha,
        LocalTime horaInicio,
        LocalTime horaFin,
        EstadoReserva estado) {

    public static ReservaResponse from(Reserva reserva) {
        return new ReservaResponse(
                reserva.getId(),
                reserva.getClienteId(),
                reserva.getProfesionalId(),
                reserva.getFecha(),
                reserva.getRango().inicio(),
                reserva.getRango().fin(),
                reserva.getEstado());
    }
}
