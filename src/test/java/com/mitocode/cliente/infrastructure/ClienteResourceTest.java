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

    @Test
    void deberiaActualizarUnClienteExistente() {
        String id = crearCliente();

        String body = """
                {"nombres":"Maria","apellidos":"Gomez","email":"maria.gomez.%s@mail.com","telefono":"111222333"}
                """.formatted(UUID.randomUUID());

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when().put("/clientes/" + id)
                .then()
                .statusCode(200)
                .body("nombres", equalTo("Maria"))
                .body("apellidos", equalTo("Gomez"))
                .body("telefono", equalTo("111222333"));
    }

    @Test
    void deberiaDevolver404AlActualizarUnClienteInexistente() {
        String body = """
                {"nombres":"Maria","apellidos":"Gomez","email":"maria.gomez.%s@mail.com","telefono":"111222333"}
                """.formatted(UUID.randomUUID());

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when().put("/clientes/" + UUID.randomUUID())
                .then()
                .statusCode(404);
    }

    @Test
    void deberiaRechazarActualizacionConDatosInvalidosCon400() {
        String id = crearCliente();

        String body = """
                {"nombres":"","apellidos":"Gomez","email":"maria.gomez@mail.com","telefono":"111222333"}
                """;

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when().put("/clientes/" + id)
                .then()
                .statusCode(400);
    }

    @Test
    void deberiaRechazarActualizacionConEmailDuplicadoCon409() {
        String primerEmail = "primero.%s@mail.com".formatted(UUID.randomUUID());
        String primerBody = """
                {"nombres":"Ana","apellidos":"Torres","email":"%s","telefono":"999888777"}
                """.formatted(primerEmail);
        given().contentType(ContentType.JSON).body(primerBody)
                .when().post("/clientes")
                .then().statusCode(201);

        String segundoId = crearCliente();

        String bodyActualizacion = """
                {"nombres":"Maria","apellidos":"Gomez","email":"%s","telefono":"111222333"}
                """.formatted(primerEmail);

        given()
                .contentType(ContentType.JSON)
                .body(bodyActualizacion)
                .when().put("/clientes/" + segundoId)
                .then()
                .statusCode(409);
    }

    @Test
    void deberiaEliminarUnClienteExistente() {
        String id = crearCliente();

        given()
                .when().delete("/clientes/" + id)
                .then()
                .statusCode(204);

        given()
                .when().get("/clientes/" + id)
                .then()
                .statusCode(200)
                .body("estadoActivo", equalTo(false));
    }

    @Test
    void deberiaDevolver404AlEliminarUnClienteInexistente() {
        given()
                .when().delete("/clientes/" + UUID.randomUUID())
                .then()
                .statusCode(404);
    }

    private String crearCliente() {
        String body = """
                {"nombres":"Ana","apellidos":"Torres","email":"ana.torres.%s@mail.com","telefono":"999888777"}
                """.formatted(UUID.randomUUID());

        return given()
                .contentType(ContentType.JSON)
                .body(body)
                .when().post("/clientes")
                .then()
                .statusCode(201)
                .extract().path("id");
    }
}
