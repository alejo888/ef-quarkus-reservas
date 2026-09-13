package com.mitocode.cliente.domain;

import com.mitocode.shared.exception.DominioException;
import jakarta.ws.rs.core.Response;

public class EmailDuplicadoException extends DominioException {

    public EmailDuplicadoException(String email) {
        super("Ya existe un cliente registrado con el email '%s'".formatted(email), Response.Status.CONFLICT);
    }
}
