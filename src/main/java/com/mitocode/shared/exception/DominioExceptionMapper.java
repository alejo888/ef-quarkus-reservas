package com.mitocode.shared.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class DominioExceptionMapper implements ExceptionMapper<DominioException> {

    @Override
    public Response toResponse(DominioException exception) {
        return Response.status(exception.getStatus())
                .type(MediaType.APPLICATION_JSON)
                .entity(new ProblemDetail(exception.getStatus().getStatusCode(), exception.getMessage()))
                .build();
    }
}
