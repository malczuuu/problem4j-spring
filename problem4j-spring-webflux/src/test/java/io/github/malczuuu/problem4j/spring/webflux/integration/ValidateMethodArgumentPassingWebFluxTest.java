package io.github.malczuuu.problem4j.spring.webflux.integration;

import static org.hamcrest.Matchers.notNullValue;

import io.github.malczuuu.problem4j.spring.webflux.app.WebFluxTestApp;
import io.github.malczuuu.problem4j.spring.webflux.app.rest.ValidateMethodArgumentController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(
    classes = {WebFluxTestApp.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ValidateMethodArgumentPassingWebFluxTest {

  @Autowired private WebTestClient webTestClient;

  /**
   * @see ValidateMethodArgumentController#validatePathVariable(String)
   */
  @Test
  void givenValidPathVariable_shouldReturnOk() {
    webTestClient
        .get()
        .uri("/validate-parameter/path-variable/value")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class)
        .value(notNullValue())
        .isEqualTo("OK");
  }

  /**
   * @see ValidateMethodArgumentController#validateRequestParam(String)
   */
  @Test
  void givenValidRequestParam_shouldReturnOk() {
    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path("/validate-parameter/request-param")
                    .queryParam("query", "value")
                    .build())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class)
        .value(notNullValue())
        .isEqualTo("OK");
  }

  /**
   * @see ValidateMethodArgumentController#validateRequestHeader(String)
   */
  @Test
  void givenValidRequestHeader_shouldReturnOk() {
    webTestClient
        .get()
        .uri("/validate-parameter/request-header")
        .header("X-Custom-Header", "value")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class)
        .value(notNullValue())
        .isEqualTo("OK");
  }

  /**
   * @see ValidateMethodArgumentController#validateCookieValue(String)
   */
  @Test
  void givenValidCookieValue_shouldReturnOk() {
    webTestClient
        .get()
        .uri("/validate-parameter/cookie-value")
        .cookie("x_session", "value")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class)
        .value(notNullValue())
        .isEqualTo("OK");
  }

  /**
   * @see ValidateMethodArgumentController#validateMultiConstraint(String)
   */
  @Test
  void givenBothParamsValid_shouldReturnOk() {
    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path("/validate-parameter/two-arg")
                    .queryParam("first", "validVal")
                    .queryParam("second", "anything")
                    .build())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class)
        .value(notNullValue())
        .isEqualTo("OK");
  }

  /**
   * @see ValidateMethodArgumentController#validateThreeArguments(String, String, String)
   */
  @Test
  void givenThreeParamsValid_shouldReturnOk() {
    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path("/validate-parameter/three-arg")
                    .queryParam("first", "anything")
                    .queryParam("second", "validVal")
                    .queryParam("third", "anything")
                    .build())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class)
        .value(notNullValue())
        .isEqualTo("OK");
  }
}
