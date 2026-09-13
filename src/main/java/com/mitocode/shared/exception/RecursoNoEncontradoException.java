package com.mitocode.shared.exception;

import jakarta.ws.rs.core.Response;

public class RecursoNoEncontradoException extends DominioException {

    public RecursoNoEncontradoException(String tipoRecurso, Object id) {
        super("%s con id '%s' no fue encontrado".formatted(tipoRecurso, id), Response.Status.NOT_FOUND);
    }
}
