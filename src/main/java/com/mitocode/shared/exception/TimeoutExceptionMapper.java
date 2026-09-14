package com.mitocode.shared.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException;
import org.jboss.logging.Logger;

@Provider
public class TimeoutExceptionMapper implements ExceptionMapper<TimeoutException> {

    private static final Logger LOG = Logger.getLogger(TimeoutExceptionMapper.class);

    @Override
    public Response toResponse(TimeoutException exception) {
        LOG.warnf("Operacion cancelada por timeout: %s", exception.getMessage());
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ProblemDetail(Response.Status.SERVICE_UNAVAILABLE.getStatusCode(),
                        "La operacion tardo demasiado en completarse. Intente nuevamente."))
                .build();
    }
}
