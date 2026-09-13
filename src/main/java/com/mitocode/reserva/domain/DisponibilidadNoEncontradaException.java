package com.mitocode.reserva.domain;

import com.mitocode.shared.exception.DominioException;
import com.mitocode.shared.exception.ExtendedStatus;
import java.time.LocalDate;
import java.util.UUID;

public class DisponibilidadNoEncontradaException extends DominioException {

    public DisponibilidadNoEncontradaException(UUID profesionalId, LocalDate fecha) {
        super("No existe un horario disponible y habilitado del profesional '%s' que cubra el intervalo solicitado el %s"
                .formatted(profesionalId, fecha), ExtendedStatus.UNPROCESSABLE_ENTITY);
    }
}
