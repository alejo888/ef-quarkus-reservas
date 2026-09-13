package com.mitocode.reserva.infrastructure;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ReservaResourceTest {

    @Test
    void deberiaCrearYLuegoCancelarUnaReserva() {
        String profesionalId = crearProfesional();
        String clienteId = crearCliente();
        crearHorario(profesionalId, "2026-06-01", "08:00:00", "12:00:00");

        String reservaBody = """
                {"clienteId":"%s","profesionalId":"%s","fecha":"2026-06-01","horaInicio":"09:00:00","horaFin":"10:00:00"}
                """.formatted(clienteId, profesionalId);

        String reservaId = given()
                .contentType(ContentType.JSON)
                .body(reservaBody)
                .when().post("/reservas")
                .then()
                .statusCode(201)
                .body("clienteId", equalTo(clienteId))
                .body("profesionalId", equalTo(profesionalId))
                .body("estado", equalTo("CREADA"))
                .extract().path("id");

        given()
                .when().post("/reservas/" + reservaId + "/cancelar")
                .then()
                .statusCode(200)
                .body("estado", equalTo("CANCELADA"));
    }

    @Test
    void deberiaRechazar404SiElClienteNoExiste() {
        String profesionalId = crearProfesional();
        crearHorario(profesionalId, "2026-06-02", "08:00:00", "12:00:00");

        String body = """
                {"clienteId":"%s","profesionalId":"%s","fecha":"2026-06-02","horaInicio":"09:00:00","horaFin":"10:00:00"}
                """.formatted(UUID.randomUUID(), profesionalId);

        given().contentType(ContentType.JSON).body(body)
                .when().post("/reservas")
                .then().statusCode(404);
    }

    @Test
    void deberiaRechazar422SiNoHayHorarioQueLaCubra() {
        String profesionalId = crearProfesional();
        String clienteId = crearCliente();

        String body = """
                {"clienteId":"%s","profesionalId":"%s","fecha":"2026-06-03","horaInicio":"09:00:00","horaFin":"10:00:00"}
                """.formatted(clienteId, profesionalId);

        given().contentType(ContentType.JSON).body(body)
                .when().post("/reservas")
                .then().statusCode(422);
    }

    @Test
    void deberiaRechazar409SiSeSolapaConOtraReservaActiva() {
        String profesionalId = crearProfesional();
        String clienteId = crearCliente();
        crearHorario(profesionalId, "2026-06-04", "08:00:00", "12:00:00");

        String primeraReserva = """
                {"clienteId":"%s","profesionalId":"%s","fecha":"2026-06-04","horaInicio":"09:00:00","horaFin":"10:00:00"}
                """.formatted(clienteId, profesionalId);
        given().contentType(ContentType.JSON).body(primeraReserva)
                .when().post("/reservas")
                .then().statusCode(201);

        String segundaReserva = """
                {"clienteId":"%s","profesionalId":"%s","fecha":"2026-06-04","horaInicio":"09:30:00","horaFin":"10:30:00"}
                """.formatted(clienteId, profesionalId);
        given().contentType(ContentType.JSON).body(segundaReserva)
                .when().post("/reservas")
                .then().statusCode(409);
    }

    @Test
    void deberiaRechazar409SiSeCancelaDosVeces() {
        String profesionalId = crearProfesional();
        String clienteId = crearCliente();
        crearHorario(profesionalId, "2026-06-05", "08:00:00", "12:00:00");

        String body = """
                {"clienteId":"%s","profesionalId":"%s","fecha":"2026-06-05","horaInicio":"09:00:00","horaFin":"10:00:00"}
                """.formatted(clienteId, profesionalId);
        String reservaId = given().contentType(ContentType.JSON).body(body)
                .when().post("/reservas")
                .then().statusCode(201)
                .extract().path("id");

        given().when().post("/reservas/" + reservaId + "/cancelar").then().statusCode(200);
        given().when().post("/reservas/" + reservaId + "/cancelar").then().statusCode(409);
    }

    private String crearProfesional() {
        String body = """
                {"nombres":"Luis","apellidos":"Salazar","especialidad":"Psicologia"}
                """;
        return given().contentType(ContentType.JSON).body(body)
                .when().post("/profesionales")
                .then().statusCode(201)
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
}
