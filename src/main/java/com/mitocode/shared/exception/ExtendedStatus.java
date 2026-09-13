package com.mitocode.shared.exception;

import jakarta.ws.rs.core.Response;

/**
 * HTTP statuses used by domain exceptions that are not part of the standard
 * {@link Response.Status} enum (e.g. 422, not defined by the JAX-RS spec).
 */
public enum ExtendedStatus implements Response.StatusType {

    UNPROCESSABLE_ENTITY(422, "Unprocessable Entity");

    private final int statusCode;
    private final String reasonPhrase;

    ExtendedStatus(int statusCode, String reasonPhrase) {
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public Response.Status.Family getFamily() {
        return Response.Status.Family.CLIENT_ERROR;
    }

    @Override
    public String getReasonPhrase() {
        return reasonPhrase;
    }
}
