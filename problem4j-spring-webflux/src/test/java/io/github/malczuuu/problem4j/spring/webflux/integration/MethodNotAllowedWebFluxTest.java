package io.github.malczuuu.problem4j.spring.webflux.integration;

import static io.github.malczuuu.problem4j.spring.webflux.integration.MethodNotAllowedWebFluxTest.MethodNotAllowedController;

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
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest(classes = {WebFluxTestApp.class})
@Import({MethodNotAllowedController.class})
@AutoConfigureWebTestClient
class MethodNotAllowedWebFluxTest {

  @RestController
  static class MethodNotAllowedController {
    @GetMapping(path = "/method-not-allowed")
    String methodNotAllowed() {
      return "OK";
    }
  }

  @Autowired private WebTestClient webTestClient;

  @Test
  void givenCallToNotAllowedMethod_shouldReturnProblem() {
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
