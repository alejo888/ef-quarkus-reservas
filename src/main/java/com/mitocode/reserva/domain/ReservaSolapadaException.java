package com.mitocode.reserva.domain;

import com.mitocode.shared.exception.DominioException;
import jakarta.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.UUID;

public class ReservaSolapadaException extends DominioException {

    public ReservaSolapadaException(UUID profesionalId, LocalDate fecha) {
        super("El profesional '%s' ya tiene una reserva activa que se solapa el %s".formatted(profesionalId, fecha),
                Response.Status.CONFLICT);
    }
}
