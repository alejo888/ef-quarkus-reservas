package com.mitocode.shared.exception;

public class EntidadInactivaException extends DominioException {

    public EntidadInactivaException(String tipoRecurso, Object id) {
        super("%s con id '%s' se encuentra inactivo".formatted(tipoRecurso, id), ExtendedStatus.UNPROCESSABLE_ENTITY);
    }
}
