package com.mitocode.cliente.domain;

import com.mitocode.shared.exception.DominioException;
import jakarta.ws.rs.core.Response;

public class EmailInvalidoException extends DominioException {

    public EmailInvalidoException(String email) {
        super("El email '%s' no tiene un formato valido".formatted(email), Response.Status.BAD_REQUEST);
    }
}
