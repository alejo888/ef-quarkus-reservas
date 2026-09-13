package com.mitocode.profesional.infrastructure;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
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
}
