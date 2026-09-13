package com.mitocode.shared.exception;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException;
import org.junit.jupiter.api.Test;

class TimeoutExceptionMapperTest {

    @Test
    void deberiaMapearTimeoutExceptionA503ConMensajeClaro() {
        TimeoutExceptionMapper mapper = new TimeoutExceptionMapper();

        Response response = mapper.toResponse(new TimeoutException("timeout"));

        assertThat(response.getStatus()).isEqualTo(503);
        ProblemDetail problem = (ProblemDetail) response.getEntity();
        assertThat(problem.status()).isEqualTo(503);
        assertThat(problem.mensaje()).isNotBlank();
    }
}
