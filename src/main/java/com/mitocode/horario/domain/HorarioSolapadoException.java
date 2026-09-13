package com.mitocode.horario.domain;

import com.mitocode.shared.exception.DominioException;
import jakarta.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.UUID;

public class HorarioSolapadoException extends DominioException {

    public HorarioSolapadoException(UUID profesionalId, LocalDate fecha) {
        super("El profesional '%s' ya tiene un horario disponible que se solapa el %s".formatted(profesionalId, fecha),
                Response.Status.CONFLICT);
    }
}
