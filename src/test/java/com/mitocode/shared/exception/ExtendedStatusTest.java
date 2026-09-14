package com.mitocode.shared.exception;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

class ExtendedStatusTest {

    @Test
    void unprocessableEntityDeberiaExponer422YFamiliaClientError() {
        assertThat(ExtendedStatus.UNPROCESSABLE_ENTITY.getStatusCode()).isEqualTo(422);
        assertThat(ExtendedStatus.UNPROCESSABLE_ENTITY.getReasonPhrase()).isNotBlank();
        assertThat(ExtendedStatus.UNPROCESSABLE_ENTITY.getFamily())
                .isEqualTo(Response.Status.Family.CLIENT_ERROR);
    }
}
