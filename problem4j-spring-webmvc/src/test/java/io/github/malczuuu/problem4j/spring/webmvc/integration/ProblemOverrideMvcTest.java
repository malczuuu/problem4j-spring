package io.github.malczuuu.problem4j.spring.webmvc.integration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.spring.webmvc.app.MvcTestApp;
import java.net.URI;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import tools.jackson.databind.json.JsonMapper;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {MvcTestApp.class},
    properties = {
      "problem4j.type-override=https://example.org/type/{problem.type}",
      "problem4j.instance-override=https://example.org/trace/{context.traceId}",
      "problem4j.tracing-header-name=X-Trace-Id"
    })
@AutoConfigureTestRestTemplate
class ProblemOverrideMvcTest {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private JsonMapper jsonMapper;

  @Test
  void givenNonEmptyType_shouldRewriteType() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<String> request = new HttpEntity<>("{}", headers);

    ResponseEntity<String> response =
        restTemplate.postForEntity("/problem-override/type-not-blank", request, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getHeaders().getContentType()).hasToString(Problem.CONTENT_TYPE);

    Problem problem = jsonMapper.readValue(response.getBody(), Problem.class);

    assertThat(problem.getType()).isEqualTo(URI.create("https://example.org/type/not-blank"));
  }

  @Test
  void givenEmptyType_shouldNotRewriteType() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<String> request = new HttpEntity<>("{}", headers);

    ResponseEntity<String> response =
        restTemplate.postForEntity("/problem-override/instance-override", request, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getHeaders().getContentType()).hasToString(Problem.CONTENT_TYPE);

    Problem problem = jsonMapper.readValue(response.getBody(), Problem.class);
    assertThat(problem.getType()).isEqualTo(Problem.BLANK_TYPE);
  }

  @Test
  void givenNonEmptyTraceId_shouldRewriteInstanceField() {
    String traceId = "12345-trace";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("X-Trace-ID", traceId);

    HttpEntity<String> request = new HttpEntity<>("{}", headers);

    ResponseEntity<String> response =
        restTemplate.postForEntity("/problem-override/instance-override", request, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getHeaders().getContentType()).hasToString(Problem.CONTENT_TYPE);
    assertThat(response.getHeaders().getFirst("X-Trace-ID")).isEqualTo(traceId);

    Problem problem = jsonMapper.readValue(response.getBody(), Problem.class);

    assertThat(problem.getInstance()).isEqualTo(URI.create("https://example.org/trace/" + traceId));
  }
}
