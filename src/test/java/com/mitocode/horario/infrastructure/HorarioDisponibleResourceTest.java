package com.mitocode.horario.infrastructure;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

@QuarkusTest
class HorarioDisponibleResourceTest {

    @Test
    void deberiaCrearUnHorarioDisponible() {
        String profesionalId = crearProfesional();

        String body = """
                {"profesionalId":"%s","fecha":"2026-05-10","horaInicio":"09:00:00","horaFin":"10:00:00"}
                """.formatted(profesionalId);

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when().post("/horarios-disponibles")
                .then()
                .statusCode(201)
                .body("profesionalId", equalTo(profesionalId))
                .body("fecha", equalTo("2026-05-10"))
                .body("estado", equalTo(true));
    }

    @Test
    void deberiaRechazarHorarioSolapadoConCodigo409() {
        String profesionalId = crearProfesional();
        crearHorario(profesionalId, "2026-05-11", "09:00:00", "11:00:00");

        String body = """
                {"profesionalId":"%s","fecha":"2026-05-11","horaInicio":"10:00:00","horaFin":"12:00:00"}
                """.formatted(profesionalId);

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when().post("/horarios-disponibles")
                .then()
                .statusCode(409);
    }

    @Test
    void deberiaPermitirHorariosNoSolapadosParaElMismoProfesional() {
        String profesionalId = crearProfesional();
        crearHorario(profesionalId, "2026-05-12", "09:00:00", "10:00:00");

        String body = """
                {"profesionalId":"%s","fecha":"2026-05-12","horaInicio":"10:00:00","horaFin":"11:00:00"}
                """.formatted(profesionalId);

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when().post("/horarios-disponibles")
                .then()
                .statusCode(201);
    }

    @Test
    void deberiaRechazarRangoInvalidoCon400() {
        String profesionalId = crearProfesional();

        String body = """
                {"profesionalId":"%s","fecha":"2026-05-13","horaInicio":"10:00:00","horaFin":"09:00:00"}
                """.formatted(profesionalId);

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when().post("/horarios-disponibles")
                .then()
                .statusCode(400);
    }

    @Test
    void deberiaRechazarDatosFaltantesCon400() {
        String body = """
                {"fecha":"2026-05-14","horaInicio":"09:00:00","horaFin":"10:00:00"}
                """;

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when().post("/horarios-disponibles")
                .then()
                .statusCode(400);
    }

    private String crearProfesional() {
        String body = """
                {"nombres":"Luis","apellidos":"Salazar","especialidad":"Psicologia"}
                """;

        return given()
                .contentType(ContentType.JSON)
                .body(body)
                .when().post("/profesionales")
                .then().statusCode(201)
                .extract().path("id");
    }

    private void crearHorario(String profesionalId, String fecha, String horaInicio, String horaFin) {
        String body = """
                {"profesionalId":"%s","fecha":"%s","horaInicio":"%s","horaFin":"%s"}
                """.formatted(profesionalId, fecha, horaInicio, horaFin);

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when().post("/horarios-disponibles")
                .then().statusCode(201);
    }
}
