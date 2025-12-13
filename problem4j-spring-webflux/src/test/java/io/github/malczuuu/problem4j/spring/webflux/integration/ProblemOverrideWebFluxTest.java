package io.github.malczuuu.problem4j.spring.webflux.integration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.spring.webflux.app.WebFluxTestApp;
import java.net.URI;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(
    classes = {WebFluxTestApp.class},
    properties = {
      "problem4j.type-override=https://example.org/type/{problem.type}",
      "problem4j.instance-override=https://example.org/trace/{context.traceId}",
      "problem4j.tracing-header-name=X-Trace-Id"
    },
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ProblemOverrideWebFluxTest {

  @Autowired private WebTestClient webTestClient;

  @Test
  void givenNonEmptyType_shouldNotRewriteType() {
    webTestClient
        .post()
        .uri("/problem-override/type-not-blank")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue("{}")
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.BAD_REQUEST)
        .expectHeader()
        .contentType(Problem.CONTENT_TYPE)
        .expectBody(Problem.class)
        .value(v -> Assertions.assertThat(v).isNotNull())
        .value(
            problem ->
                assertThat(problem.getType())
                    .isEqualTo(URI.create("https://example.org/type/not-blank")));
  }

  @Test
  void givenEmptyType_shouldNotRewriteType() {
    webTestClient
        .post()
        .uri("/problem-override/instance-override")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue("{}")
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.BAD_REQUEST)
        .expectHeader()
        .contentType(Problem.CONTENT_TYPE)
        .expectBody(Problem.class)
        .value(v -> assertThat(v).isNotNull())
        .value(problem -> assertThat(problem.getType()).isEqualTo(Problem.BLANK_TYPE));
  }

  @Test
  void givenNonEmptyTraceId_shouldRewriteInstanceField() {
    String traceId = "12345-trace";
    webTestClient
        .post()
        .uri("/problem-override/instance-override")
        .header("X-Trace-Id", traceId)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue("{}")
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.BAD_REQUEST)
        .expectHeader()
        .contentType(Problem.CONTENT_TYPE)
        .expectHeader()
        .value("X-Trace-Id", v -> assertThat(v).isEqualTo(traceId))
        .expectBody(Problem.class)
        .value(v -> assertThat(v).isNotNull())
        .value(
            problem ->
                assertThat(problem.getInstance())
                    .isEqualTo(URI.create("https://example.org/trace/" + traceId)));
  }
}
