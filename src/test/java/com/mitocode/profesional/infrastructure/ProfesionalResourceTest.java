package com.mitocode.profesional.infrastructure;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ProfesionalResourceTest {

    @Test
    void deberiaCrearYLuegoObtenerUnProfesional() {
        String body = """
                {"nombres":"Luis","apellidos":"Salazar","especialidad":"Psicologia"}
                """;

        String id = given()
                .contentType(ContentType.JSON)
                .body(body)
                .when().post("/profesionales")
                .then()
                .statusCode(201)
                .body("nombres", equalTo("Luis"))
                .body("apellidos", equalTo("Salazar"))
                .body("especialidad", equalTo("Psicologia"))
                .body("estadoActivo", equalTo(true))
                .body("id", notNullValue())
                .extract().path("id");

        given()
                .when().get("/profesionales/" + id)
                .then()
                .statusCode(200)
                .body("apellidos", equalTo("Salazar"));
    }

    @Test
    void deberiaDevolver404SiElProfesionalNoExiste() {
        given()
                .when().get("/profesionales/" + UUID.randomUUID())
                .then()
                .statusCode(404);
    }

    @Test
    void deberiaRechazarDatosInvalidosCon400() {
        String body = """
                {"nombres":"","apellidos":"Salazar","especialidad":"Psicologia"}
                """;

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when().post("/profesionales")
                .then()
                .statusCode(400);
    }

    @Test
    void deberiaListarProfesionalesOrdenadosPorReservasActivasDescendente() {
        String conDosReservasId = crearProfesional("Con", "DosReservas");
        String conUnaReservaId = crearProfesional("Con", "UnaReserva");
        String sinReservasId = crearProfesional("Sin", "Reservas");

        String clienteId = crearCliente();

        crearHorario(conDosReservasId, "2026-07-01", "08:00:00", "12:00:00");
        crearReserva(clienteId, conDosReservasId, "2026-07-01", "08:00:00", "09:00:00");
        crearReserva(clienteId, conDosReservasId, "2026-07-01", "09:00:00", "10:00:00");

        crearHorario(conUnaReservaId, "2026-07-01", "08:00:00", "12:00:00");
        crearReserva(clienteId, conUnaReservaId, "2026-07-01", "08:00:00", "09:00:00");

        List<String> idsEnOrden = given()
                .when().get("/profesionales")
                .then()
                .statusCode(200)
                .body("find { it.id == '%s' }.reservasActivas".formatted(conDosReservasId), equalTo(2))
                .body("find { it.id == '%s' }.reservasActivas".formatted(conUnaReservaId), equalTo(1))
                .body("find { it.id == '%s' }.reservasActivas".formatted(sinReservasId), equalTo(0))
                .extract().body().jsonPath().getList("id", String.class);

        List<String> idsDeInteresEnOrden = idsEnOrden.stream()
                .filter(id -> id.equals(conDosReservasId) || id.equals(conUnaReservaId) || id.equals(sinReservasId))
                .toList();

        assertThat(idsDeInteresEnOrden).containsExactly(conDosReservasId, conUnaReservaId, sinReservasId);
    }

    @Test
    void deberiaListarProfesionalesCorrectamenteConElTimeoutConfigurado() {
        String id = crearProfesional("Timeout", "Feliz");

        given()
                .when().get("/profesionales")
                .then()
                .statusCode(200)
                .body("find { it.id == '%s' }".formatted(id), notNullValue());
    }

    @Test
    void deberiaActualizarUnProfesionalExistente() {
        String id = crearProfesional("Luis", "Salazar");

        String body = """
                {"nombres":"Carlos","apellidos":"Mendez","especialidad":"Nutricion"}
                """;

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when().put("/profesionales/" + id)
                .then()
                .statusCode(200)
                .body("nombres", equalTo("Carlos"))
                .body("apellidos", equalTo("Mendez"))
                .body("especialidad", equalTo("Nutricion"));
    }

    @Test
    void deberiaDevolver404AlActualizarUnProfesionalInexistente() {
        String body = """
                {"nombres":"Carlos","apellidos":"Mendez","especialidad":"Nutricion"}
                """;

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when().put("/profesionales/" + UUID.randomUUID())
                .then()
                .statusCode(404);
    }

    @Test
    void deberiaRechazarActualizacionConDatosInvalidosCon400() {
        String id = crearProfesional("Luis", "Salazar");

        String body = """
                {"nombres":"","apellidos":"Mendez","especialidad":"Nutricion"}
                """;

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when().put("/profesionales/" + id)
                .then()
                .statusCode(400);
    }

    @Test
    void deberiaEliminarUnProfesionalExistente() {
        String id = crearProfesional("Luis", "Salazar");

        given()
                .when().delete("/profesionales/" + id)
                .then()
                .statusCode(204);

        given()
                .when().get("/profesionales/" + id)
                .then()
                .statusCode(200)
                .body("estadoActivo", equalTo(false));
    }

    @Test
    void deberiaDevolver404AlEliminarUnProfesionalInexistente() {
        given()
                .when().delete("/profesionales/" + UUID.randomUUID())
                .then()
                .statusCode(404);
    }

    private String crearProfesional(String nombres, String apellidos) {
        String body = """
                {"nombres":"%s","apellidos":"%s-%s","especialidad":"Psicologia"}
                """.formatted(nombres, apellidos, UUID.randomUUID());

        return given()
                .contentType(ContentType.JSON)
                .body(body)
                .when().post("/profesionales")
                .then()
                .statusCode(201)
                .extract().path("id");
    }

    private String crearCliente() {
        String body = """
                {"nombres":"Ana","apellidos":"Torres","email":"ana.%s@mail.com","telefono":"999888777"}
                """.formatted(UUID.randomUUID());
        return given().contentType(ContentType.JSON).body(body)
                .when().post("/clientes")
                .then().statusCode(201)
                .extract().path("id");
    }

    private void crearHorario(String profesionalId, String fecha, String horaInicio, String horaFin) {
        String body = """
                {"profesionalId":"%s","fecha":"%s","horaInicio":"%s","horaFin":"%s"}
                """.formatted(profesionalId, fecha, horaInicio, horaFin);
        given().contentType(ContentType.JSON).body(body)
                .when().post("/horarios-disponibles")
                .then().statusCode(201);
    }

    private void crearReserva(String clienteId, String profesionalId, String fecha, String horaInicio,
            String horaFin) {
        String body = """
                {"clienteId":"%s","profesionalId":"%s","fecha":"%s","horaInicio":"%s","horaFin":"%s"}
                """.formatted(clienteId, profesionalId, fecha, horaInicio, horaFin);
        given().contentType(ContentType.JSON).body(body)
                .when().post("/reservas")
                .then().statusCode(201);
    }
}
