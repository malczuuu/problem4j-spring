package io.github.malczuuu.problem4j.spring.webflux.integration;

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
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest(classes = {_TestApp.class})
@Import({MethodNotAllowedTest.MethodNotAllowedController.class})
@AutoConfigureWebTestClient
class MethodNotAllowedTest {

  @RestController
  static class MethodNotAllowedController {

    @GetMapping(path = "/method-not-allowed")
    String endpoint() {
      return "OK";
    }
  }

  @Autowired private WebTestClient webTestClient;

  @Test
  void givenException_shouldOverrideIt() {
    webTestClient
        .post()
        .uri("/method-not-allowed")
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.METHOD_NOT_ALLOWED)
        .expectHeader()
        .contentType(Problem.CONTENT_TYPE)
        .expectBody(Problem.class)
        .isEqualTo(Problem.builder().status(ProblemStatus.METHOD_NOT_ALLOWED).build());
  }
}
