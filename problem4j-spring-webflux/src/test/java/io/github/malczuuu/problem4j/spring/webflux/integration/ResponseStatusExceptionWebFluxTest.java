package io.github.malczuuu.problem4j.spring.webflux.integration;

import static io.github.malczuuu.problem4j.spring.webflux.integration.ResponseStatusExceptionWebFluxTest.ResponseStatusController;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.webflux.app.WebFluxTestApp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest(
    classes = {WebFluxTestApp.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({ResponseStatusController.class})
@AutoConfigureWebTestClient
class ResponseStatusExceptionWebFluxTest {

  @RestController
  static class ResponseStatusController {
    @GetMapping("/response-status-exception")
    String endpoint(@RequestParam(value = "reason", required = false) String reason) {
      if (reason == null) {
        throw new ResponseStatusException(HttpStatus.GONE);
      }
      throw new ResponseStatusException(HttpStatus.GONE, reason);
    }
  }

  @Autowired private WebTestClient webTestClient;

  @Test
  void givenResponseStatusException_shouldReturnProblem() {
    webTestClient
        .get()
        .uri("/response-status-exception")
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.GONE)
        .expectHeader()
        .contentType(Problem.CONTENT_TYPE)
        .expectBody(Problem.class)
        .value(v -> assertThat(v).isNotNull())
        .isEqualTo(Problem.builder().status(ProblemStatus.GONE).build());
  }

  @Test
  void givenResponseStatusExceptionWithReason_returnProblemWithStatusOnly() {
    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder.path("/response-status-exception").queryParam("reason", "gone").build())
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.GONE)
        .expectHeader()
        .contentType(Problem.CONTENT_TYPE)
        .expectBody(Problem.class)
        .value(v -> assertThat(v).isNotNull())
        .isEqualTo(Problem.builder().status(ProblemStatus.GONE).build());
  }
}
