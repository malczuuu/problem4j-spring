package io.github.malczuuu.problem4j.spring.webflux.integration;

import static io.github.malczuuu.problem4j.spring.webflux.integration.ResponseStatusAnnotatedExceptionWebFluxTest.AnnotatedStatusController;
import static org.hamcrest.Matchers.notNullValue;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.webflux.app.WebFluxTestApp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest(
    classes = {WebFluxTestApp.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({AnnotatedStatusController.class})
@AutoConfigureWebTestClient
class ResponseStatusAnnotatedExceptionWebFluxTest {

  @ResponseStatus(HttpStatus.FORBIDDEN)
  static class ForbiddenAnnotatedException extends RuntimeException {}

  @ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "this is reason")
  static class ReasonAnnotatedException extends RuntimeException {}

  @RestController
  static class AnnotatedStatusController {

    @GetMapping("/response-status-annotated")
    String responseStatusAnnotated() {
      throw new ForbiddenAnnotatedException();
    }

    @GetMapping("/reason-annotated")
    String reasonAnnotated() {
      throw new ReasonAnnotatedException();
    }
  }

  @Autowired private WebTestClient webTestClient;

  @Test
  void givenSpringNativeResponseStatusAnnotation_shouldReturnProblemWithStatus() {
    webTestClient
        .get()
        .uri("/response-status-annotated")
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
        .uri("/reason-annotated")
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
