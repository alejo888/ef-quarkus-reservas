package com.mitocode.profesional.infrastructure;

import static io.restassured.RestAssured.given;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestProfile(RepositorioLentoTestProfile.class)
class ProfesionalResourceTimeoutTest {

    @Test
    void deberiaResponder503SiLaConsultaExcedeElTimeoutConfigurado() {
        given()
                .when().get("/profesionales")
                .then()
                .statusCode(503);
    }
}
