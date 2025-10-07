package io.github.malczuuu.problem4j.spring.webflux.integration;

import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.ERRORS_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.VALIDATION_FAILED_DETAIL;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest(
    classes = {_TestApp.class},
    properties = {
      "problem4j.instance-override=https://example.org/trace/{traceId}",
      "problem4j.tracing-header-name=X-Trace-Id"
    })
@Import({InstanceOverrideTest.InstanceOverrideController.class})
@AutoConfigureWebTestClient
class InstanceOverrideTest {

  record TestRequest(@NotBlank String name) {}

  @RestController
  static class InstanceOverrideController {
    @PostMapping("/instance-override")
    String endpoint(@Valid @RequestBody TestRequest request) {
      return "OK";
    }
  }

  @Autowired private WebTestClient webTestClient;

  @Test
  void givenInvalidRequestBody_shouldReturnProblemWithViolations() {
    String traceId = "12345-trace";

    TestRequest invalidRequest = new TestRequest("");

    webTestClient
        .post()
        .uri("/instance-override")
        .header("X-Trace-Id", traceId)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(invalidRequest)
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.BAD_REQUEST)
        .expectHeader()
        .contentType(Problem.CONTENT_TYPE)
        .expectHeader()
        .value("X-Trace-Id", v -> assertThat(v).isEqualTo(traceId))
        .expectBody(Problem.class)
        .value(
            problem ->
                assertThat(problem)
                    .isEqualTo(
                        Problem.builder()
                            .status(ProblemStatus.BAD_REQUEST)
                            .detail(VALIDATION_FAILED_DETAIL)
                            .instance("https://example.org/trace/" + traceId)
                            .extension(
                                ERRORS_EXTENSION,
                                List.of(Map.of("field", "name", "error", "must not be blank")))
                            .build()));
  }
}
