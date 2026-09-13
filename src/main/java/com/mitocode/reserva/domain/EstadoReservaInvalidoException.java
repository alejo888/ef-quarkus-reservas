package com.mitocode.reserva.domain;

import com.mitocode.shared.exception.DominioException;
import jakarta.ws.rs.core.Response;

public class EstadoReservaInvalidoException extends DominioException {

    public EstadoReservaInvalidoException(EstadoReserva estadoActual) {
        super("La reserva se encuentra en estado '%s' y no admite esa transicion".formatted(estadoActual),
                Response.Status.CONFLICT);
    }
}
