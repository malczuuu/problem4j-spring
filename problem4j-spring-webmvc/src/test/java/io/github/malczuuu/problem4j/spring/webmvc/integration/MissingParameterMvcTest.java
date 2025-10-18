package io.github.malczuuu.problem4j.spring.webmvc.integration;

import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.ATTRIBUTE_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.COOKIE_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.HEADER_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.KIND_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.MISSING_COOKIE_DETAIL;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.MISSING_HEADER_DETAIL;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.MISSING_PATH_VARIABLE_DETAIL;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.MISSING_REQUEST_ATTRIBUTE_DETAIL;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.MISSING_REQUEST_PARAM_DETAIL;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.MISSING_REQUEST_PART_DETAIL;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.MISSING_SESSION_ATTRIBUTE_DETAIL;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.NAME_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.PARAM_EXTENSION;
import static io.github.malczuuu.problem4j.spring.webmvc.integration.MissingParameterMvcTest.MissingParameterController;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.webmvc.app.MvcTestApp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.test.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.json.JsonMapper;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {MvcTestApp.class})
@Import({MissingParameterController.class})
class MissingParameterMvcTest {

  @RestController
  static class MissingParameterController {

    @GetMapping(
        path = {"/missing-parameter/path-variable", "/missing-parameter/path-variable/{var}"})
    String missingPathVariable(@PathVariable("var") String var) {
      return "OK";
    }

    @GetMapping(path = "/missing-parameter/request-param")
    String missingRequestParam(@RequestParam("param") String param) {
      return "OK";
    }

    @PostMapping(path = "/missing-parameter/request-part")
    String missingRequestPart(@RequestPart("file") MultipartFile file) {
      return "OK";
    }

    @GetMapping(path = "/missing-parameter/request-header")
    String missingRequestHeader(@RequestHeader("X-Custom-Header") String xCustomHeader) {
      return "OK";
    }

    @GetMapping(path = "/missing-parameter/cookie-value")
    String missingCookieValue(@CookieValue("x_session") String xSession) {
      return "OK";
    }

    @GetMapping(path = "/missing-parameter/request-attribute")
    String missingRequestAttribute(@RequestAttribute("attr") String attr) {
      return "OK";
    }

    @GetMapping(path = "/missing-parameter/session-attribute")
    String missingSessionAttribute(@SessionAttribute("attr") String attr) {
      return "OK";
    }
  }

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private JsonMapper jsonMapper;

  @Test
  void givenRequestWithoutPathVariable_shouldReturnProblem() {
    ResponseEntity<String> response =
        restTemplate.getForEntity("/missing-parameter/path-variable", String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getHeaders().getContentType()).hasToString(Problem.CONTENT_TYPE);

    Problem problem = jsonMapper.readValue(response.getBody(), Problem.class);

    assertThat(problem)
        .isEqualTo(
            Problem.builder()
                .status(ProblemStatus.BAD_REQUEST)
                .detail(MISSING_PATH_VARIABLE_DETAIL)
                .extension(NAME_EXTENSION, "var")
                .build());
  }

  @Test
  void givenRequestWithPathVariable_shouldReturnOk() {
    ResponseEntity<String> response =
        restTemplate.getForEntity("/missing-parameter/path-variable/value", String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("OK");
  }

  @Test
  void givenRequestWithoutRequestParam_shouldReturnProblem() {
    ResponseEntity<String> response =
        restTemplate.getForEntity("/missing-parameter/request-param", String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getHeaders().getContentType()).hasToString(Problem.CONTENT_TYPE);

    Problem problem = jsonMapper.readValue(response.getBody(), Problem.class);

    assertThat(problem)
        .isEqualTo(
            Problem.builder()
                .status(ProblemStatus.BAD_REQUEST)
                .detail(MISSING_REQUEST_PARAM_DETAIL)
                .extension(PARAM_EXTENSION, "param")
                .extension(KIND_EXTENSION, "string")
                .build());
  }

  @Test
  void givenRequestWithRequestParam_shouldReturnOk() {
    ResponseEntity<String> response =
        restTemplate.getForEntity("/missing-parameter/request-param?param=value", String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("OK");
  }

  @Test
  void givenRequestWithoutRequestPartParam_shouldReturnProblem() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    HttpEntity<MultiValueMap<String, Object>> request =
        new HttpEntity<>(new LinkedMultiValueMap<>(), headers);

    ResponseEntity<String> response =
        restTemplate.postForEntity("/missing-parameter/request-part", request, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getHeaders().getContentType()).hasToString(Problem.CONTENT_TYPE);

    Problem problem = jsonMapper.readValue(response.getBody(), Problem.class);

    assertThat(problem)
        .isEqualTo(
            Problem.builder()
                .status(ProblemStatus.BAD_REQUEST)
                .detail(MISSING_REQUEST_PART_DETAIL)
                .extension(PARAM_EXTENSION, "file")
                .build());
  }

  @Test
  void givenRequestWithoutRequestPartHeader_shouldReturnProblem() {
    ResponseEntity<String> response =
        restTemplate.postForEntity(
            "/missing-parameter/request-part",
            new HttpEntity<>(null, new HttpHeaders()),
            String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getHeaders().getContentType()).hasToString(Problem.CONTENT_TYPE);

    Problem problem = jsonMapper.readValue(response.getBody(), Problem.class);

    assertThat(problem).isEqualTo(Problem.builder().status(ProblemStatus.BAD_REQUEST).build());
  }

  @Test
  void givenRequestWithRequestPart_shouldReturnOk() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add(
        "file",
        new ByteArrayResource("test content".getBytes()) {
          @Override
          public String getFilename() {
            return "test.txt";
          }
        });
    HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

    ResponseEntity<String> response =
        restTemplate.postForEntity("/missing-parameter/request-part", request, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("OK");
  }

  @Test
  void givenRequestWithoutRequestHeader_shouldReturnProblem() {
    ResponseEntity<String> response =
        restTemplate.getForEntity("/missing-parameter/request-header", String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getHeaders().getContentType()).hasToString(Problem.CONTENT_TYPE);
    Problem problem = jsonMapper.readValue(response.getBody(), Problem.class);
    assertThat(problem)
        .isEqualTo(
            Problem.builder()
                .status(ProblemStatus.BAD_REQUEST)
                .detail(MISSING_HEADER_DETAIL)
                .extension(HEADER_EXTENSION, "X-Custom-Header")
                .build());
  }

  @Test
  void givenRequestWithRequestHeader_shouldReturnOk() {
    HttpHeaders headers = new HttpHeaders();
    headers.set("X-Custom-Header", "value");
    HttpEntity<Void> request = new HttpEntity<>(headers);
    ResponseEntity<String> response =
        restTemplate.exchange(
            "/missing-parameter/request-header", HttpMethod.GET, request, String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("OK");
  }

  @Test
  void givenRequestWithoutCookieValue_shouldReturnProblem() {
    ResponseEntity<String> response =
        restTemplate.getForEntity("/missing-parameter/cookie-value", String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getHeaders().getContentType()).hasToString(Problem.CONTENT_TYPE);
    Problem problem = jsonMapper.readValue(response.getBody(), Problem.class);
    assertThat(problem)
        .isEqualTo(
            Problem.builder()
                .status(ProblemStatus.BAD_REQUEST)
                .detail(MISSING_COOKIE_DETAIL)
                .extension(COOKIE_EXTENSION, "x_session")
                .build());
  }

  @Test
  void givenRequestWithCookieValue_shouldReturnOk() {
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.COOKIE, "x_session=value");
    HttpEntity<Void> request = new HttpEntity<>(headers);
    ResponseEntity<String> response =
        restTemplate.exchange(
            "/missing-parameter/cookie-value", HttpMethod.GET, request, String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("OK");
  }

  @Test
  void givenRequestWithoutRequestAttribute_shouldReturnProblem() {
    ResponseEntity<String> response =
        restTemplate.getForEntity("/missing-parameter/request-attribute", String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getHeaders().getContentType()).hasToString(Problem.CONTENT_TYPE);
    Problem problem = jsonMapper.readValue(response.getBody(), Problem.class);
    assertThat(problem)
        .isEqualTo(
            Problem.builder()
                .status(ProblemStatus.BAD_REQUEST)
                .detail(MISSING_REQUEST_ATTRIBUTE_DETAIL)
                .extension(ATTRIBUTE_EXTENSION, "attr")
                .build());
  }

  @Test
  void givenRequestWithoutSessionAttribute_shouldReturnProblem() {
    ResponseEntity<String> response =
        restTemplate.getForEntity("/missing-parameter/session-attribute", String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getHeaders().getContentType()).hasToString(Problem.CONTENT_TYPE);
    Problem problem = jsonMapper.readValue(response.getBody(), Problem.class);
    assertThat(problem)
        .isEqualTo(
            Problem.builder()
                .status(ProblemStatus.BAD_REQUEST)
                .detail(MISSING_SESSION_ATTRIBUTE_DETAIL)
                .extension(ATTRIBUTE_EXTENSION, "attr")
                .build());
  }
}
