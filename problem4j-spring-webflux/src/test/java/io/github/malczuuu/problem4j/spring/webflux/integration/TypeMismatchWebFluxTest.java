package io.github.malczuuu.problem4j.spring.webflux.integration;

import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.KIND_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.PROPERTY_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.TYPE_MISMATCH_DETAIL;
import static org.hamcrest.Matchers.notNullValue;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.webflux.app.WebFluxTestApp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(
    classes = {WebFluxTestApp.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class TypeMismatchWebFluxTest {

  @Autowired private WebTestClient webTestClient;

  @Test
  void givenRequestWithInvalidPathVariable_shouldReturnProblem() {
    webTestClient
        .get()
        .uri(uriBuilder -> uriBuilder.path("/type-mismatch/path-variable/abc").build())
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.BAD_REQUEST)
        .expectHeader()
        .contentType(Problem.CONTENT_TYPE)
        .expectBody(Problem.class)
        .value(notNullValue())
        .isEqualTo(
            Problem.builder()
                .status(ProblemStatus.BAD_REQUEST)
                .detail(TYPE_MISMATCH_DETAIL)
                .extension(PROPERTY_EXTENSION, "id")
                .extension(KIND_EXTENSION, "integer")
                .build());
  }

  @Test
  void givenRequestWithValidPathVariable_shouldReturnOk() {
    webTestClient
        .get()
        .uri(uriBuilder -> uriBuilder.path("/type-mismatch/path-variable/123").build())
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.OK)
        .expectBody(String.class)
        .value(notNullValue())
        .isEqualTo("OK");
  }

  @Test
  void givenRequestWithInvalidParameterType_shouldReturnProblem() {
    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder.path("/type-mismatch/request-param").queryParam("id", "abc").build())
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.BAD_REQUEST)
        .expectHeader()
        .contentType(Problem.CONTENT_TYPE)
        .expectBody(Problem.class)
        .value(notNullValue())
        .isEqualTo(
            Problem.builder()
                .status(ProblemStatus.BAD_REQUEST)
                .detail(TYPE_MISMATCH_DETAIL)
                .extension(PROPERTY_EXTENSION, "id")
                .extension(KIND_EXTENSION, "integer")
                .build());
  }

  @Test
  void givenRequestWithValidParameter_shouldReturnOk() {
    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder.path("/type-mismatch/request-param").queryParam("id", "123").build())
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.OK)
        .expectBody(String.class)
        .value(notNullValue())
        .isEqualTo("OK");
  }

  @Test
  void givenRequestWithInvalidRequestHeader_shouldReturnProblem() {
    webTestClient
        .get()
        .uri(uriBuilder -> uriBuilder.path("/type-mismatch/request-header").build())
        .header("X-Id", "abc")
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.BAD_REQUEST)
        .expectHeader()
        .contentType(Problem.CONTENT_TYPE)
        .expectBody(Problem.class)
        .value(notNullValue())
        .isEqualTo(
            Problem.builder()
                .status(ProblemStatus.BAD_REQUEST)
                .detail(TYPE_MISMATCH_DETAIL)
                .extension(PROPERTY_EXTENSION, "X-Id")
                .extension(KIND_EXTENSION, "integer")
                .build());
  }

  @Test
  void givenRequestWithValidRequestHeader_shouldReturnOk() {
    webTestClient
        .get()
        .uri(uriBuilder -> uriBuilder.path("/type-mismatch/request-header").build())
        .header("X-Id", "123")
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.OK)
        .expectBody(String.class)
        .value(notNullValue())
        .isEqualTo("OK");
  }

  @Test
  void givenRequestWithInvalidCookieValue_shouldReturnProblem() {
    webTestClient
        .get()
        .uri(uriBuilder -> uriBuilder.path("/type-mismatch/cookie-value").build())
        .cookie("id", "abc")
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.BAD_REQUEST)
        .expectHeader()
        .contentType(Problem.CONTENT_TYPE)
        .expectBody(Problem.class)
        .value(notNullValue())
        .isEqualTo(
            Problem.builder()
                .status(ProblemStatus.BAD_REQUEST)
                .detail(TYPE_MISMATCH_DETAIL)
                .extension(PROPERTY_EXTENSION, "id")
                .extension(KIND_EXTENSION, "integer")
                .build());
  }

  @Test
  void givenRequestWithValidCookieValue_shouldReturnOk() {
    webTestClient
        .get()
        .uri(uriBuilder -> uriBuilder.path("/type-mismatch/cookie-value").build())
        .cookie("id", "123")
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.OK)
        .expectBody(String.class)
        .value(notNullValue())
        .isEqualTo("OK");
  }
}
