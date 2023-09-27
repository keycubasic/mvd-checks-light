package org.keycube.checkpassport.resource;

import static io.restassured.RestAssured.given;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
class PassportResourceTest {

  @Test
  void checkFound() {
    given()
        .when()
        .get("/mvd-checks/expired-passport/check?serial=0101&number=413910")
        .then()
        .statusCode(302);
  }

  @Test
  void checkNotFound() {
    given()
        .when()
        .get("/mvd-checks/expired-passport/check?serial=1111&number=111112")
        .then()
        .statusCode(200);
  }
}
