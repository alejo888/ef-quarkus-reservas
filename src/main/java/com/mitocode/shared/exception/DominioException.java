package com.mitocode.shared.exception;

import jakarta.ws.rs.core.Response;

/**
 * Base type for every business-rule violation raised by the domain layer.
 * Carries the HTTP status the ExceptionMapper should use, so domain code
 * states its own severity without leaking JAX-RS response building.
 */
public abstract class DominioException extends RuntimeException {

    private final Response.StatusType status;

    protected DominioException(String message, Response.StatusType status) {
        super(message);
        this.status = status;
    }

    public Response.StatusType getStatus() {
        return status;
    }
}
