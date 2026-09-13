package com.mitocode.shared.logging;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.container.ContainerRequestFilter;
import org.jboss.logging.Logger;
import org.jboss.logging.MDC;

@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOG = Logger.getLogger(LoggingFilter.class);
    private static final String START_TIME_PROPERTY = "com.mitocode.shared.logging.startTime";

    @Override
    public void filter(ContainerRequestContext requestContext) {
        requestContext.setProperty(START_TIME_PROPERTY, System.currentTimeMillis());

        String method = requestContext.getMethod();
        String path = requestContext.getUriInfo().getPath();

        LOG.infof("Entrada de peticion: %s %s", method, path);
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        String method = requestContext.getMethod();
        String path = requestContext.getUriInfo().getPath();
        int status = responseContext.getStatus();

        Object startTime = requestContext.getProperty(START_TIME_PROPERTY);
        long durationMs = startTime instanceof Long inicio ? System.currentTimeMillis() - inicio : -1L;

        try {
            MDC.put("httpMethod", method);
            MDC.put("httpPath", path);
            MDC.put("httpStatus", status);
            MDC.put("durationMs", durationMs);

            LOG.infof("Salida de respuesta: %s %s status=%d durationMs=%d", method, path, status, durationMs);
        } finally {
            MDC.clear();
        }
    }
}
