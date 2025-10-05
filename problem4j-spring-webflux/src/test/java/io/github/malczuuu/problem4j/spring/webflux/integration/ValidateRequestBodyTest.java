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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
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

@SpringBootTest(classes = {_TestApp.class})
@Import({ValidateRequestBodyTest.ValidateRequestBodyController.class})
@AutoConfigureWebTestClient
class ValidateRequestBodyTest {

  record TestRequest(@NotBlank String name) {}

  @RestController
  static class ValidateRequestBodyController {
    @PostMapping("/validate-request-body")
    String endpoint(@Valid @RequestBody TestRequest request) {
      return "OK";
    }
  }

  @Autowired private WebTestClient webTestClient;

  @Test
  void givenInvalidRequestBody_shouldReturnProblemWithViolations() {
    TestRequest invalidRequest = new TestRequest("");

    webTestClient
        .post()
        .uri("/validate-request-body")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(invalidRequest)
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.BAD_REQUEST)
        .expectHeader()
        .contentType(Problem.CONTENT_TYPE)
        .expectBody(Problem.class)
        .value(
            problem ->
                assertThat(problem)
                    .isEqualTo(
                        Problem.builder()
                            .status(ProblemStatus.BAD_REQUEST)
                            .detail(VALIDATION_FAILED_DETAIL)
                            .extension(
                                ERRORS_EXTENSION,
                                List.of(Map.of("field", "name", "error", "must not be blank")))
                            .build()));
  }

  @ParameterizedTest
  @ValueSource(strings = {"{ \"name\": \"Alice\"", ""})
  @NullSource
  void givenMalformedRequestBody_shouldReturnProblemWithViolations(String json) {
    WebTestClient.RequestBodySpec spec =
        webTestClient.post().uri("/validate-request-body").contentType(MediaType.APPLICATION_JSON);

    if (json != null) {
      spec.bodyValue(json);
    }

    spec.exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.BAD_REQUEST)
        .expectHeader()
        .contentType(Problem.CONTENT_TYPE)
        .expectBody(Problem.class)
        .isEqualTo(Problem.builder().status(ProblemStatus.BAD_REQUEST).build());
  }
}
