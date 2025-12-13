package io.github.malczuuu.problem4j.spring.webmvc.integration;

import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.ERRORS_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.VALIDATION_FAILED_DETAIL;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.webmvc.app.MvcTestApp;
import io.github.malczuuu.problem4j.spring.webmvc.app.model.AlwaysInvalidRequest;
import io.github.malczuuu.problem4j.spring.webmvc.app.model.TestRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
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
    classes = {MvcTestApp.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class ValidateRequestBodyMvcTest {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private JsonMapper jsonMapper;

  @Test
  void givenInvalidRequestBody_shouldReturnProblem() {
    TestRequest invalidRequest = new TestRequest("", null);

    ResponseEntity<String> response =
        restTemplate.postForEntity("/validate-request-body", invalidRequest, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getHeaders().getContentType()).hasToString(Problem.CONTENT_TYPE);

    Problem problem = jsonMapper.readValue(response.getBody(), Problem.class);

    assertThat(problem)
        .isEqualTo(
            Problem.builder()
                .status(ProblemStatus.BAD_REQUEST)
                .detail(VALIDATION_FAILED_DETAIL)
                .extension(
                    ERRORS_EXTENSION,
                    List.of(Map.of("field", "name", "error", "must not be blank")))
                .build());
  }

  @Test
  void givenGlobalValidationViolation_shouldReturnProblemWithoutFieldName() {
    AlwaysInvalidRequest body = new AlwaysInvalidRequest("value");

    ResponseEntity<String> response =
        restTemplate.postForEntity("/validate-global-object", body, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getHeaders().getContentType()).hasToString(Problem.CONTENT_TYPE);

    Problem problem = jsonMapper.readValue(response.getBody(), Problem.class);

    Map<String, String> error = new HashMap<>();
    error.put("field", null);
    error.put("error", "always invalid");
    assertThat(problem)
        .isEqualTo(
            Problem.builder()
                .status(ProblemStatus.BAD_REQUEST)
                .detail(VALIDATION_FAILED_DETAIL)
                .extension(ERRORS_EXTENSION, List.of(error))
                .build());
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"{ \"name\": \"Alice\"", "{ \"name\": \"Alice\", \"age\": \"too young\"}", ""})
  @NullSource
  void givenMalformedRequestBody_shouldReturnProblem(String json) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<String> entity = new HttpEntity<>(json, headers);

    ResponseEntity<String> response =
        restTemplate.postForEntity("/validate-request-body", entity, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getHeaders().getContentType()).hasToString(Problem.CONTENT_TYPE);

    Problem problem = jsonMapper.readValue(response.getBody(), Problem.class);

    assertThat(problem).isEqualTo(Problem.builder().status(ProblemStatus.BAD_REQUEST).build());
  }
}
