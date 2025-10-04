package io.github.malczuuu.problem4j.spring.webflux.integration;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest(classes = {_TestApp.class})
@Import({ErrorResponseTest.ErrorResponseController.class})
@AutoConfigureWebTestClient
class ErrorResponseTest {

  @RestController
  static class ErrorResponseController {
    @GetMapping("/error-response")
    String endpoint() {
      throw new ErrorResponseException(
          HttpStatus.CONFLICT,
          ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "this is detail"),
          null);
    }
  }

  @Autowired private WebTestClient webTestClient;

  @Test
  void givenException_shouldOverrideIt() {
    webTestClient
        .get()
        .uri("/error-response")
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.CONFLICT)
        .expectHeader()
        .contentType(Problem.CONTENT_TYPE)
        .expectBody(Problem.class)
        .isEqualTo(
            Problem.builder().status(ProblemStatus.CONFLICT).detail("this is detail").build());
  }
}
