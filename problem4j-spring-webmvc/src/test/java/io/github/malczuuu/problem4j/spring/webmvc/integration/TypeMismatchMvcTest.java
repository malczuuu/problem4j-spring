package io.github.malczuuu.problem4j.spring.webmvc.integration;

import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.KIND_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.PROPERTY_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.TYPE_MISMATCH_DETAIL;
import static io.github.malczuuu.problem4j.spring.webmvc.integration.TypeMismatchMvcTest.TypeMismatchController;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.json.JsonMapper;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {MvcTestApp.class})
@Import({TypeMismatchController.class})
@AutoConfigureTestRestTemplate
class TypeMismatchMvcTest {

  @RestController
  static class TypeMismatchController {

    @GetMapping(path = "/type-mismatch/path-variable/{id}")
    String pathVariable(@PathVariable("id") Integer id) {
      return "OK";
    }

    @GetMapping(path = "/type-mismatch/request-param")
    String requestParam(@RequestParam("id") Integer id) {
      return "OK";
    }

    @GetMapping(path = "/type-mismatch/request-header")
    String requestHeader(@RequestHeader("X-Id") Integer id) {
      return "OK";
    }

    @GetMapping(path = "/type-mismatch/cookie-value")
    String cookieValue(@CookieValue("id") Integer id) {
      return "OK";
    }
  }

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private JsonMapper jsonMapper;

  @Test
  void givenRequestWithInvalidPathVariable_shouldReturnProblem() {
    ResponseEntity<String> response =
        restTemplate.getForEntity("/type-mismatch/path-variable/abc", String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getHeaders().getContentType()).hasToString(Problem.CONTENT_TYPE);

    Problem problem = jsonMapper.readValue(response.getBody(), Problem.class);

    assertThat(problem)
        .isEqualTo(
            Problem.builder()
                .status(ProblemStatus.BAD_REQUEST)
                .detail(TYPE_MISMATCH_DETAIL)
                .extension(PROPERTY_EXTENSION, "id")
                .extension(KIND_EXTENSION, "integer")
                .build());
  }

  @Test
  void givenRequestWithValidPathVariable_shouldReturnOk() {
    ResponseEntity<String> response =
        restTemplate.getForEntity("/type-mismatch/path-variable/123", String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("OK");
  }

  @Test
  void givenRequestWithInvalidParameterType_shouldReturnProblem() {
    ResponseEntity<String> response =
        restTemplate.getForEntity("/type-mismatch/request-param?id=abc", String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getHeaders().getContentType()).hasToString(Problem.CONTENT_TYPE);

    Problem problem = jsonMapper.readValue(response.getBody(), Problem.class);

    assertThat(problem)
        .isEqualTo(
            Problem.builder()
                .status(ProblemStatus.BAD_REQUEST)
                .detail(TYPE_MISMATCH_DETAIL)
                .extension(PROPERTY_EXTENSION, "id")
                .extension(KIND_EXTENSION, "integer")
                .build());
  }

  @Test
  void givenRequestWithValidParameter_shouldReturnOk() {
    ResponseEntity<String> response =
        restTemplate.getForEntity("/type-mismatch/request-param?id=123", String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("OK");
  }

  @Test
  void givenRequestWithInvalidRequestHeader_shouldReturnProblem() {
    HttpHeaders headers = new HttpHeaders();
    headers.set("X-Id", "abc");

    HttpEntity<Void> request = new HttpEntity<>(headers);

    ResponseEntity<String> response =
        restTemplate.exchange(
            "/type-mismatch/request-header", HttpMethod.GET, request, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getHeaders().getContentType()).hasToString(Problem.CONTENT_TYPE);

    Problem problem = jsonMapper.readValue(response.getBody(), Problem.class);

    assertThat(problem)
        .isEqualTo(
            Problem.builder()
                .status(ProblemStatus.BAD_REQUEST)
                .detail(TYPE_MISMATCH_DETAIL)
                .extension(PROPERTY_EXTENSION, "X-Id")
                .extension(KIND_EXTENSION, "integer")
                .build());
  }

  @Test
  void givenRequestWithValidRequestHeader_shouldReturnOk() {
    HttpHeaders headers = new HttpHeaders();
    headers.set("X-Id", "123");

    HttpEntity<Void> request = new HttpEntity<>(headers);

    ResponseEntity<String> response =
        restTemplate.exchange(
            "/type-mismatch/request-header", HttpMethod.GET, request, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("OK");
  }

  @Test
  void givenRequestWithInvalidCookieValue_shouldReturnProblem() {
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.COOKIE, "id=abc");

    HttpEntity<Void> request = new HttpEntity<>(headers);

    ResponseEntity<String> response =
        restTemplate.exchange("/type-mismatch/cookie-value", HttpMethod.GET, request, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getHeaders().getContentType()).hasToString(Problem.CONTENT_TYPE);

    Problem problem = jsonMapper.readValue(response.getBody(), Problem.class);

    assertThat(problem)
        .isEqualTo(
            Problem.builder()
                .status(ProblemStatus.BAD_REQUEST)
                .detail(TYPE_MISMATCH_DETAIL)
                .extension(PROPERTY_EXTENSION, "id")
                .extension(KIND_EXTENSION, "integer")
                .build());
  }

  @Test
  void givenRequestWithValidCookieValue_shouldReturnOk() {
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.COOKIE, "id=123");

    HttpEntity<Void> request = new HttpEntity<>(headers);

    ResponseEntity<String> response =
        restTemplate.exchange("/type-mismatch/cookie-value", HttpMethod.GET, request, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("OK");
  }
}
