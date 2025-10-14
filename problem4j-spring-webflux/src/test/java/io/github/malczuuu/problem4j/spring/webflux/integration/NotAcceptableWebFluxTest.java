package io.github.malczuuu.problem4j.spring.webflux.integration;

import static io.github.malczuuu.problem4j.spring.webflux.integration.NotAcceptableWebFluxTest.NotAcceptableController;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.webflux.app.WebFluxTestApp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest(
    classes = {WebFluxTestApp.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({NotAcceptableController.class})
@AutoConfigureWebTestClient
class NotAcceptableWebFluxTest {

  @RestController
  static class NotAcceptableController {
    @GetMapping(path = "/not-acceptable", produces = MediaType.TEXT_PLAIN_VALUE)
    String notAcceptable() {
      return "OK";
    }
  }

  @Autowired private WebTestClient webTestClient;

  @Test
  void givenUnsupportedAcceptHeader_shouldReturnProblem() {
    webTestClient
        .get()
        .uri("/not-acceptable")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.NOT_ACCEPTABLE)
        .expectHeader()
        .contentType(Problem.CONTENT_TYPE)
        .expectBody(Problem.class)
        .isEqualTo(Problem.builder().status(ProblemStatus.NOT_ACCEPTABLE).build());
  }
}
