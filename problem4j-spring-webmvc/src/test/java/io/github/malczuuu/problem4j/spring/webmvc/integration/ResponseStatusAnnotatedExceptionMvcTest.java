package io.github.malczuuu.problem4j.spring.webmvc.integration;

import static io.github.malczuuu.problem4j.spring.webmvc.integration.ResponseStatusAnnotatedExceptionMvcTest.AnnotatedStatusController;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.webmvc.app.MvcTestApp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.json.JsonMapper;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {MvcTestApp.class})
@Import({AnnotatedStatusController.class})
@AutoConfigureTestRestTemplate
class ResponseStatusAnnotatedExceptionMvcTest {

  @ResponseStatus(HttpStatus.FORBIDDEN)
  static class ForbiddenAnnotatedException extends RuntimeException {}

  @ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "this is reason")
  static class ReasonAnnotatedException extends RuntimeException {}

  @RestController
  static class AnnotatedStatusController {

    @GetMapping("/response-status-annotated")
    String responseStatusAnnotated() {
      throw new ForbiddenAnnotatedException();
    }

    @GetMapping("/reason-annotated")
    String reasonAnnotated() {
      throw new ReasonAnnotatedException();
    }
  }

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private JsonMapper jsonMapper;

  @Test
  void givenSpringNativeResponseStatusAnnotation_shouldReturnProblemWithStatus() {
    ResponseEntity<String> response =
        restTemplate.getForEntity("/response-status-annotated", String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    assertThat(response.getHeaders().getContentType()).hasToString(Problem.CONTENT_TYPE);

    Problem problem = jsonMapper.readValue(response.getBody(), Problem.class);

    assertThat(problem).isEqualTo(Problem.builder().status(ProblemStatus.FORBIDDEN).build());
  }

  @Test
  void givenSpringNativeResponseStatusAnnotationWithReason_shouldReturnProblem() {
    ResponseEntity<String> response = restTemplate.getForEntity("/reason-annotated", String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    assertThat(response.getHeaders().getContentType()).hasToString(Problem.CONTENT_TYPE);

    Problem problem = jsonMapper.readValue(response.getBody(), Problem.class);

    assertThat(problem)
        .isEqualTo(
            Problem.builder().status(ProblemStatus.FORBIDDEN).detail("this is reason").build());
  }
}
