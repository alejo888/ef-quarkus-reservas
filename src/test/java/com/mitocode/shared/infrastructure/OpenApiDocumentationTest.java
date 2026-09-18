package com.mitocode.shared.infrastructure;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
class OpenApiDocumentationTest {

    @Test
    void openApiSpecDeberiaIncluirTagsYOperacionesDocumentadas() {
        given()
                .when().get("/openapi")
                .then()
                .statusCode(200)
                .body(containsString("Profesionales"))
                .body(containsString("Clientes"))
                .body(containsString("Horarios disponibles"))
                .body(containsString("Reservas"))
                .body(containsString("Completar reserva"))
                .body(containsString("Cancelar reserva"));
    }
}
