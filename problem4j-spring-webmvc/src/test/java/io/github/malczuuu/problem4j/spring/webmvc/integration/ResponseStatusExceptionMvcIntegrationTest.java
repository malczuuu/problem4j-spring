package io.github.malczuuu.problem4j.spring.webmvc.integration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.webmvc.app.MvcTestApp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {MvcTestApp.class})
@Import({ResponseStatusExceptionMvcIntegrationTest.ResponseStatusController.class})
class ResponseStatusExceptionMvcIntegrationTest {

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
  @Autowired private ObjectMapper objectMapper;

  @Test
  void givenResponseStatusException_shouldReturnProblem() throws Exception {
    ResponseEntity<String> response =
        restTemplate.getForEntity("/response-status-exception", String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.GONE);
    assertThat(response.getHeaders().getContentType()).hasToString(Problem.CONTENT_TYPE);

    Problem problem = objectMapper.readValue(response.getBody(), Problem.class);

    assertThat(problem).isEqualTo(Problem.builder().status(ProblemStatus.GONE).build());
  }

  @Test
  void givenResponseStatusExceptionWithReason_returnProblemWithStatusOnly() throws Exception {
    ResponseEntity<String> response =
        restTemplate.getForEntity(
            "/response-status-exception?reason=resource%20gone", String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.GONE);
    assertThat(response.getHeaders().getContentType()).hasToString(Problem.CONTENT_TYPE);

    Problem problem = objectMapper.readValue(response.getBody(), Problem.class);

    assertThat(problem).isEqualTo(Problem.builder().status(ProblemStatus.GONE).build());
  }
}
