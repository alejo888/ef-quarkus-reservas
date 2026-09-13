package com.mitocode.shared.logging;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import io.quarkus.test.junit.QuarkusTest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;

@QuarkusTest
class LoggingFilterTest {

    @Test
    void deberiaRegistrarEntradaYSalidaEstructuradaParaUnaPeticionHttp() {
        List<LogRecord> registros = new ArrayList<>();
        Handler handler = new Handler() {
            @Override
            public void publish(LogRecord record) {
                registros.add(record);
            }

            @Override
            public void flush() {
                // no-op
            }

            @Override
            public void close() {
                // no-op
            }
        };

        Logger julLogger = Logger.getLogger(LoggingFilter.class.getName());
        julLogger.addHandler(handler);
        julLogger.setLevel(java.util.logging.Level.ALL);

        try {
            given()
                    .when().get("/profesionales/" + UUID.randomUUID())
                    .then()
                    .statusCode(404);

            List<String> mensajes = registros.stream().map(LogRecord::getMessage).toList();

            boolean entradaRegistrada = mensajes.stream()
                    .anyMatch(mensaje -> mensaje.contains("GET") && mensaje.contains("/profesionales/"));

            boolean salidaRegistrada = mensajes.stream()
                    .anyMatch(mensaje -> mensaje.contains("GET") && mensaje.contains("/profesionales/")
                            && mensaje.contains("404"));

            assertThat(registros).isNotEmpty();
            assertThat(entradaRegistrada).isTrue();
            assertThat(salidaRegistrada).isTrue();
        } finally {
            julLogger.removeHandler(handler);
        }
    }
}
