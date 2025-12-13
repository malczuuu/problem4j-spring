package io.github.malczuuu.problem4j.spring.webflux.integration;

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
class ResponseStatusAnnotatedExceptionWebFluxTest {

  @Autowired private WebTestClient webTestClient;

  @Test
  void givenSpringNativeResponseStatusAnnotation_shouldReturnProblemWithStatus() {
    webTestClient
        .get()
        .uri("/response-status-annotated/forbidden-status-annotated")
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.FORBIDDEN)
        .expectHeader()
        .contentType(Problem.CONTENT_TYPE)
        .expectBody(Problem.class)
        .value(notNullValue())
        .isEqualTo(Problem.builder().status(ProblemStatus.FORBIDDEN).build());
  }

  @Test
  void givenSpringNativeResponseStatusAnnotationWithReason_shouldReturnProblem() {
    webTestClient
        .get()
        .uri("/response-status-annotated/reason-annotated")
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.FORBIDDEN)
        .expectHeader()
        .contentType(Problem.CONTENT_TYPE)
        .expectBody(Problem.class)
        .value(notNullValue())
        .isEqualTo(
            Problem.builder().status(ProblemStatus.FORBIDDEN).detail("this is reason").build());
  }
}
