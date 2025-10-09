package io.github.malczuuu.problem4j.spring.webflux.integration;

import static io.github.malczuuu.problem4j.spring.webflux.integration.ResponseStatusAnnotatedExceptionWebFluxTest.AnnotatedStatusController;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
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

@SpringBootTest(classes = {_TestApp.class})
@Import({AnnotatedStatusController.class})
@AutoConfigureWebTestClient
class ResponseStatusAnnotatedExceptionWebFluxTest {

  @ResponseStatus(HttpStatus.FORBIDDEN)
  static class ForbiddenAnnotatedException extends RuntimeException {}

  @RestController
  static class AnnotatedStatusController {
    @GetMapping("/response-status-annotated")
    String endpoint() {
      throw new ForbiddenAnnotatedException();
    }
  }

  @Autowired private WebTestClient webTestClient;

  // FIXME: support for @ResponseStatus is not implemented yet
  @Test
  void givenSpringNativeResponseStatusAnnotation_shouldReturnProblemWithStatus() {
    webTestClient
        .get()
        .uri("/response-status-annotated")
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
        .expectHeader()
        .contentType(Problem.CONTENT_TYPE)
        .expectBody(Problem.class)
        .isEqualTo(Problem.builder().status(ProblemStatus.INTERNAL_SERVER_ERROR).build());
  }
}
