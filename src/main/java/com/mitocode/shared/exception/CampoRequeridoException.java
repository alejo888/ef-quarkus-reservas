package com.mitocode.shared.exception;

import jakarta.ws.rs.core.Response;

public class CampoRequeridoException extends DominioException {

    public CampoRequeridoException(String campo) {
        super("El campo '%s' es obligatorio".formatted(campo), Response.Status.BAD_REQUEST);
    }
}
