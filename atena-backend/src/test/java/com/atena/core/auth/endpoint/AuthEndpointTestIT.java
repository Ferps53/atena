package com.atena.core.auth.endpoint;

import static io.restassured.RestAssured.given;

import io.quarkus.test.junit.QuarkusTest;
import java.util.Base64;
import org.junit.jupiter.api.*;

@QuarkusTest
@Tag("integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthEndpointTestIT {

  private static final String BASIC_USERNAME = "Test";

  private static final String BASIC_PASSWORD = "Test";

  @Test
  @Order(1)
  void loginOk() {
    given()
        .header("Authorization", "Basic " + generateBasic())
        .param("usernameOrEmail", "test")
        .param("password", "12345678")
        .get("/auth/login")
        .then()
        .statusCode(200);
  }

  @Test
  @Order(1)
  void login401Basic() {
    given()
        .header("Authorization", "Basic 123")
        .param("usernameOrEmail", "test")
        .param("password", "12345678")
        .get("/auth/login")
        .then()
        .statusCode(401);
  }

  @Test
  @Order(1)
  void loginUserNotSignedIn() {
    given()
        .header("Authorization", "Basic " + generateBasic())
        .param("usernameOrEmail", "notFound")
        .param("password", "11111")
        .get("/auth/login")
        .then()
        .statusCode(400);
  }

  @Test
  @Order(2)
  void signIn() {
    given()
        .header("Authorization", "Basic " + generateBasic())
        .param("username", "test2")
        .param("email", "test2@gmail.com")
        .param("password", "12345678")
        .when()
        .get("/auth/sign-in")
        .then()
        .statusCode(200);
  }

  @Test
  @Order(3)
  void loginWithNewUser() {
    given()
        .header("Authorization", "Basic " + generateBasic())
        .param("usernameOrEmail", "test2")
        .param("password", "12345678")
        .get("/auth/login")
        .then()
        .statusCode(200);
  }

  String generateBasic() {
    return Base64.getEncoder().encodeToString((BASIC_USERNAME + ":" + BASIC_PASSWORD).getBytes());
  }
}
