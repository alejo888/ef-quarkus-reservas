package com.mitocode.shared.domain;

import com.mitocode.shared.exception.DominioException;
import jakarta.ws.rs.core.Response;
import java.time.LocalTime;

public class RangoHorarioInvalidoException extends DominioException {

    public RangoHorarioInvalidoException(LocalTime inicio, LocalTime fin) {
        super("La hora de inicio (%s) debe ser anterior a la hora de fin (%s)".formatted(inicio, fin),
                Response.Status.BAD_REQUEST);
    }
}
