package io.github.malczuuu.problem4j.spring.webmvc.integration;

import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.ERRORS_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.VALIDATION_FAILED_DETAIL;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.webmvc.app.MvcTestApp;
import io.github.malczuuu.problem4j.spring.webmvc.integration.ValidateMethodArgumentFailingMvcTest.ValidateParameterController;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.test.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.json.JsonMapper;

@SpringBootTest(
    classes = {MvcTestApp.class},
    properties = "spring.validation.method.adapt-constraint-violations=false",
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({ValidateParameterController.class})
class ValidateMethodArgumentFailingMvcTest {

  private static final String VIOLATION_ERROR = "size must be between 5 and " + Integer.MAX_VALUE;

  @Validated
  @RestController
  static class ValidateParameterController {

    @GetMapping("/validate-parameter/path-variable/{id}")
    String validatePathVariable(@PathVariable("id") @Size(min = 5) String idVar) {
      return "OK";
    }

    @GetMapping("/validate-parameter/request-param")
    String validateRequestParam(@RequestParam("query") @Size(min = 5) String queryParam) {
      return "OK";
    }

    @GetMapping("/validate-parameter/request-header")
    String validateRequestHeader(
        @RequestHeader("X-Custom-Header") @Size(min = 5) String xCustomHeader) {
      return "OK";
    }

    @GetMapping("/validate-parameter/cookie-value")
    String validateCookieValue(@CookieValue("x_session") @Size(min = 5) String xSession) {
      return "OK";
    }

    @GetMapping("/validate-parameter/multi-constraint")
    String validateMultiConstraint(
        @RequestParam("input") @Size(min = 5) @Pattern(regexp = "i") String inputParam) {
      return "OK";
    }

    @GetMapping("/validate-parameter/two-arg")
    String validateTwoArguments(
        @RequestParam("first") @Size(min = 5) String firstParam,
        @RequestParam("second") String secondParam) {
      return "OK";
    }

    @GetMapping("/validate-parameter/three-arg")
    String validateThreeArguments(
        @RequestParam("first") String firstParam,
        @RequestParam("second") @Size(min = 5) String secondParam,
        @RequestParam("third") String thirdParam) {
      return "OK";
    }
  }

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private JsonMapper jsonMapper;

  @Test
  void givenTooShortPathVariable_shouldReturnValidationProblem() {
    ResponseEntity<String> response =
        restTemplate.getForEntity("/validate-parameter/path-variable/v", String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getHeaders().getContentType()).hasToString(Problem.CONTENT_TYPE);

    Problem problem = jsonMapper.readValue(response.getBody(), Problem.class);

    assertThat(problem)
        .isEqualTo(
            Problem.builder()
                .status(ProblemStatus.BAD_REQUEST)
                .detail(VALIDATION_FAILED_DETAIL)
                .extension(
                    ERRORS_EXTENSION, List.of(Map.of("field", "idVar", "error", VIOLATION_ERROR)))
                .build());
  }

  @Test
  void givenTooShortRequestParam_shouldReturnValidationProblem() {
    ResponseEntity<String> response =
        restTemplate.getForEntity("/validate-parameter/request-param?query=v", String.class);

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
                    List.of(Map.of("field", "queryParam", "error", VIOLATION_ERROR)))
                .build());
  }

  @Test
  void givenTooShortRequestHeader_shouldReturnValidationProblem() {
    HttpHeaders headers = new HttpHeaders();
    headers.set("X-Custom-Header", "v");

    ResponseEntity<String> response =
        restTemplate.exchange(
            "/validate-parameter/request-header",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            String.class);

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
                    List.of(Map.of("field", "xCustomHeader", "error", VIOLATION_ERROR)))
                .build());
  }

  @Test
  void givenTooShortCookieValue_shouldReturnValidationProblem() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Cookie", "x_session=v");

    ResponseEntity<String> response =
        restTemplate.exchange(
            "/validate-parameter/cookie-value",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            String.class);

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
                    List.of(Map.of("field", "xSession", "error", VIOLATION_ERROR)))
                .build());
  }

  @Test
  void givenValueViolatingAllConstraints_shouldReturnAllErrors() {
    ResponseEntity<String> response =
        restTemplate.getForEntity("/validate-parameter/multi-constraint?input=v", String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getHeaders().getContentType()).hasToString(Problem.CONTENT_TYPE);

    Problem problem = jsonMapper.readValue(response.getBody(), Problem.class);

    assertThat(problem.getExtensionValue(ERRORS_EXTENSION)).asInstanceOf(LIST).hasSize(2);
  }

  @ParameterizedTest
  @ValueSource(strings = {"vvvvv", "iiiii"})
  void givenValueViolatingSingleConstraint_shouldReturnCorrectError(String input) {
    ResponseEntity<String> response =
        restTemplate.getForEntity(
            "/validate-parameter/multi-constraint?input=" + input, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getHeaders().getContentType()).hasToString(Problem.CONTENT_TYPE);

    Problem problem = jsonMapper.readValue(response.getBody(), Problem.class);

    assertThat(problem.getExtensionValue(ERRORS_EXTENSION)).asInstanceOf(LIST).hasSize(1);
  }

  @Test
  void givenFirstParamTooShort_shouldReturnValidationError() {
    ResponseEntity<String> response =
        restTemplate.getForEntity(
            "/validate-parameter/two-arg?first=v&second=anything", String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getHeaders().getContentType()).hasToString(Problem.CONTENT_TYPE);

    Problem problem = jsonMapper.readValue(response.getBody(), Problem.class);

    assertThat(problem.getExtensionValue(ERRORS_EXTENSION))
        .asInstanceOf(LIST)
        .hasSize(1)
        .allSatisfy(e -> assertThat(((Map<?, ?>) e).get("field")).isEqualTo("firstParam"));
  }

  @Test
  void givenSecondParamTooShort_shouldReturnValidationError() {
    ResponseEntity<String> response =
        restTemplate.getForEntity(
            "/validate-parameter/three-arg?first=anything&second=v&third=anything", String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getHeaders().getContentType()).hasToString(Problem.CONTENT_TYPE);

    Problem problem = jsonMapper.readValue(response.getBody(), Problem.class);

    assertThat(problem.getExtensionValue(ERRORS_EXTENSION))
        .asInstanceOf(LIST)
        .hasSize(1)
        .allSatisfy(e -> assertThat(((Map<?, ?>) e).get("field")).isEqualTo("secondParam"));
  }

  @Test
  void givenBothParamsValid_shouldReturnOk() {
    ResponseEntity<String> response =
        restTemplate.getForEntity(
            "/validate-parameter/two-arg?first=validVal&second=anything", String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("OK");
  }
}
