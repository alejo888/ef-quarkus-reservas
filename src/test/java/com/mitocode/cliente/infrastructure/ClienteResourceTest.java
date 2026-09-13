package com.mitocode.cliente.infrastructure;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ClienteResourceTest {

    @Test
    void deberiaCrearYLuegoObtenerUnCliente() {
        String body = """
                {"nombres":"Ana","apellidos":"Torres","email":"ana.torres.%s@mail.com","telefono":"999888777"}
                """.formatted(UUID.randomUUID());

        String id = given()
                .contentType(ContentType.JSON)
                .body(body)
                .when().post("/clientes")
                .then()
                .statusCode(201)
                .body("nombres", equalTo("Ana"))
                .body("apellidos", equalTo("Torres"))
                .body("telefono", equalTo("999888777"))
                .body("estadoActivo", equalTo(true))
                .body("id", notNullValue())
                .extract().path("id");

        given()
                .when().get("/clientes/" + id)
                .then()
                .statusCode(200)
                .body("apellidos", equalTo("Torres"));
    }

    @Test
    void deberiaDevolver404SiElClienteNoExiste() {
        given()
                .when().get("/clientes/" + UUID.randomUUID())
                .then()
                .statusCode(404);
    }

    @Test
    void deberiaRechazarDatosInvalidosCon400() {
        String body = """
                {"nombres":"","apellidos":"Torres","email":"ana@mail.com","telefono":"999888777"}
                """;

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when().post("/clientes")
                .then()
                .statusCode(400);
    }

    @Test
    void deberiaRechazarEmailConFormatoInvalidoCon400() {
        String body = """
                {"nombres":"Ana","apellidos":"Torres","email":"no-es-un-email","telefono":"999888777"}
                """;

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when().post("/clientes")
                .then()
                .statusCode(400);
    }

    @Test
    void deberiaRechazarEmailDuplicadoCon409() {
        String email = "duplicado.%s@mail.com".formatted(UUID.randomUUID());
        String body = """
                {"nombres":"Ana","apellidos":"Torres","email":"%s","telefono":"999888777"}
                """.formatted(email);

        given().contentType(ContentType.JSON).body(body)
                .when().post("/clientes")
                .then().statusCode(201);

        given().contentType(ContentType.JSON).body(body)
                .when().post("/clientes")
                .then().statusCode(409);
    }
}
