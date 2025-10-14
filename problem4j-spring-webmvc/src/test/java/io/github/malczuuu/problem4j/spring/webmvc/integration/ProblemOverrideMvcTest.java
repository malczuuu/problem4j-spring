package io.github.malczuuu.problem4j.spring.webmvc.integration;

import static io.github.malczuuu.problem4j.spring.webmvc.integration.ProblemOverrideMvcTest.InstanceOverrideController;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemException;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.webmvc.app.MvcTestApp;
import java.net.URI;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {MvcTestApp.class},
    properties = {
      "problem4j.type-override=https://example.org/type/{problem.type}",
      "problem4j.instance-override=https://example.org/trace/{context.traceId}",
      "problem4j.tracing-header-name=X-Trace-Id"
    })
@Import({InstanceOverrideController.class})
class ProblemOverrideMvcTest {

  @RestController
  static class InstanceOverrideController {
    @PostMapping(path = "/instance-override")
    String instanceOverride() {
      throw new ProblemException(Problem.builder().status(ProblemStatus.BAD_REQUEST).build());
    }

    @PostMapping(path = "/type-not-blank")
    String typeNotBlank() {
      throw new ProblemException(
          Problem.builder().type("not-blank").status(ProblemStatus.BAD_REQUEST).build());
    }
  }

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void givenNonEmptyType_shouldRewriteType() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<String> request = new HttpEntity<>("{}", headers);

    ResponseEntity<String> response =
        restTemplate.postForEntity("/type-not-blank", request, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getHeaders().getContentType()).hasToString(Problem.CONTENT_TYPE);

    Problem problem = objectMapper.readValue(response.getBody(), Problem.class);

    assertThat(problem.getType()).isEqualTo(URI.create("https://example.org/type/not-blank"));
  }

  @Test
  void givenEmptyType_shouldNotRewriteType() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<String> request = new HttpEntity<>("{}", headers);

    ResponseEntity<String> response =
        restTemplate.postForEntity("/instance-override", request, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getHeaders().getContentType()).hasToString(Problem.CONTENT_TYPE);

    Problem problem = objectMapper.readValue(response.getBody(), Problem.class);
    assertThat(problem.getType()).isEqualTo(Problem.BLANK_TYPE);
  }

  @Test
  void givenNonEmptyTraceId_shouldRewriteInstanceField() throws Exception {
    String traceId = "12345-trace";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("X-Trace-ID", traceId);

    HttpEntity<String> request = new HttpEntity<>("{}", headers);

    ResponseEntity<String> response =
        restTemplate.postForEntity("/instance-override", request, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getHeaders().getContentType()).hasToString(Problem.CONTENT_TYPE);
    assertThat(response.getHeaders().getFirst("X-Trace-ID")).isEqualTo(traceId);

    Problem problem = objectMapper.readValue(response.getBody(), Problem.class);

    assertThat(problem.getInstance()).isEqualTo(URI.create("https://example.org/trace/" + traceId));
  }
}
