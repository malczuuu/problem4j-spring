package io.github.malczuuu.problem4j.spring.webflux.integration;

import static io.github.malczuuu.problem4j.spring.webflux.integration.ValidateMethodArgumentPassingWebFluxTest.ValidateParameterController;

import io.github.malczuuu.problem4j.spring.webflux.app.WebFluxTestApp;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest(classes = {WebFluxTestApp.class})
@Import({ValidateParameterController.class})
@AutoConfigureWebTestClient
class ValidateMethodArgumentPassingWebFluxTest {

  @Validated
  @RestController
  static class ValidateParameterController {

    @GetMapping("/validate-parameter/path-variable/{id}")
    String validatePathVariable(@PathVariable("id") @Size(min = 5) String idVar) {
      return "OK";
    }

    @GetMapping("/validate-parameter/request-param")
    String validateRequestParam(@RequestParam("query") @Size(min = 5) String queryParam) {
      return "OK";
    }

    @GetMapping("/validate-parameter/request-header")
    String validateRequestHeader(
        @RequestHeader("X-Custom-Header") @Size(min = 5) String xCustomHeader) {
      return "OK";
    }

    @GetMapping("/validate-parameter/cookie-value")
    String validateCookieValue(@CookieValue("x_session") @Size(min = 5) String xSession) {
      return "OK";
    }

    @GetMapping("/validate-parameter/multi-constraint")
    String validateMultiConstraint(
        @RequestParam("input") @Size(min = 5) @Pattern(regexp = "i") String inputParam) {
      return "OK";
    }

    @GetMapping("/validate-parameter/two-arg")
    String validateTwoArguments(
        @RequestParam("first") @Size(min = 5) String firstParam,
        @RequestParam("second") String secondParam) {
      return "OK";
    }

    @GetMapping("/validate-parameter/three-arg")
    String validateThreeArguments(
        @RequestParam("first") String firstParam,
        @RequestParam("second") @Size(min = 5) String secondParam,
        @RequestParam("third") String thirdParam) {
      return "OK";
    }
  }

  @Autowired private WebTestClient webTestClient;

  /**
   * @see ValidateParameterController#validatePathVariable(String)
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
        .isEqualTo("OK");
  }

  /**
   * @see ValidateParameterController#validateRequestParam(String)
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
        .isEqualTo("OK");
  }

  /**
   * @see ValidateParameterController#validateRequestHeader(String)
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
        .isEqualTo("OK");
  }

  /**
   * @see ValidateParameterController#validateCookieValue(String)
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
        .isEqualTo("OK");
  }

  /**
   * @see ValidateParameterController#validateMultiConstraint(String)
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
        .isEqualTo("OK");
  }

  /**
   * @see ValidateParameterController#validateThreeArguments(String, String, String)
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
        .isEqualTo("OK");
  }
}
