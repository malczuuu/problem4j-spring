package io.github.malczuuu.problem4j.spring.webmvc.integration;

import static io.github.malczuuu.problem4j.spring.webmvc.integration.ResponseStatusExceptionMvcTest.ResponseStatusController;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.json.JsonMapper;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {MvcTestApp.class})
@Import({ResponseStatusController.class})
@AutoConfigureTestRestTemplate
class ResponseStatusExceptionMvcTest {

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

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private JsonMapper jsonMapper;

  @Test
  void givenResponseStatusException_shouldReturnProblem() {
    ResponseEntity<String> response =
        restTemplate.getForEntity("/response-status-exception", String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.GONE);
    assertThat(response.getHeaders().getContentType()).hasToString(Problem.CONTENT_TYPE);

    Problem problem = jsonMapper.readValue(response.getBody(), Problem.class);

    assertThat(problem).isEqualTo(Problem.builder().status(ProblemStatus.GONE).build());
  }

  @Test
  void givenResponseStatusExceptionWithReason_returnProblemWithStatusOnly() {
    ResponseEntity<String> response =
        restTemplate.getForEntity(
            "/response-status-exception?reason=resource%20gone", String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.GONE);
    assertThat(response.getHeaders().getContentType()).hasToString(Problem.CONTENT_TYPE);

    Problem problem = jsonMapper.readValue(response.getBody(), Problem.class);

    assertThat(problem).isEqualTo(Problem.builder().status(ProblemStatus.GONE).build());
  }
}
