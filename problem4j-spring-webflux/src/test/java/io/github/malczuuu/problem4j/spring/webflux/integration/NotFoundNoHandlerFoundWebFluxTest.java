package io.github.malczuuu.problem4j.spring.webflux.integration;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.webflux.app.WebFluxTestApp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(
    classes = {WebFluxTestApp.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class NotFoundNoHandlerFoundWebFluxTest {

  @Autowired private WebTestClient webTestClient;

  @Test
  void givenUnknownPath_shouldReturnProblem() {
    webTestClient
        .get()
        .uri("/not-found-controller")
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectHeader()
        .contentType(Problem.CONTENT_TYPE)
        .expectBody(Problem.class)
        .value(v -> assertThat(v).isNotNull())
        .isEqualTo(Problem.builder().status(ProblemStatus.NOT_FOUND).build());
  }
}
