package io.github.malczuuu.problem4j.spring.webflux.integration;

import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.ERRORS_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.VALIDATION_FAILED_DETAIL;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

class ValidateParameterTest {

  private static final String VIOLATION_ERROR = "size must be between 5 and " + Integer.MAX_VALUE;

  @Validated
  @RestController
  static class PathVariableController {
    @GetMapping("/validate-parameter/path-variable/{id}")
    String endpoint(@PathVariable("id") @Size(min = 5) String idVar) {
      return "OK";
    }
  }

  @Validated
  @RestController
  static class RequestParamController {
    @GetMapping("/validate-parameter/request-param")
    String endpoint(@RequestParam("query") @Size(min = 5) String queryParam) {
      return "OK";
    }
  }

  @Validated
  @RestController
  static class RequestHeaderController {
    @GetMapping("/validate-parameter/request-header")
    String endpoint(@RequestHeader("X-Custom-Header") @Size(min = 5) String xCustomHeader) {
      return "OK";
    }
  }

  @Validated
  @RestController
  static class CookieValueController {
    @GetMapping("/validate-parameter/cookie-value")
    String endpoint(@CookieValue("x_session") @Size(min = 5) String xSession) {
      return "OK";
    }
  }

  @Validated
  @RestController
  static class MultiConstraintController {
    @GetMapping("/validate-parameter/multi-constraint")
    String endpoint(
        @RequestParam("input") @Size(min = 5) @Pattern(regexp = "i") String inputParam) {
      return "OK";
    }
  }

  @Validated
  @RestController
  static class TwoArgController {
    @GetMapping("/validate-parameter/two-arg")
    String endpoint(
        @RequestParam("first") @Size(min = 5) String firstParam,
        @RequestParam("second") String secondParam) {
      return "OK";
    }
  }

  @Validated
  @RestController
  static class ThreeArgController {
    @GetMapping("/validate-parameter/three-arg")
    String endpoint(
        @RequestParam("first") String firstParam,
        @RequestParam("second") @Size(min = 5) String secondParam,
        @RequestParam("third") String thirdParam) {
      return "OK";
    }
  }

  @Nested
  @SpringBootTest(classes = {_TestApp.class})
  @Import({
    PathVariableController.class,
    RequestParamController.class,
    RequestHeaderController.class,
    CookieValueController.class
  })
  @AutoConfigureWebTestClient
  class ValidationPassed {

    @Autowired private WebTestClient webTestClient;

    /**
     * @see PathVariableController
     */
    @Test
    void givenValidPathVariable_shouldReturnOk() {
      webTestClient
          .get()
          .uri("/validate-parameter/path-variable/value")
          .exchange()
          .expectStatus()
          .isOk()
          .expectBody(String.class)
          .isEqualTo("OK");
    }

    /**
     * @see RequestParamController
     */
    @Test
    void givenValidRequestParam_shouldReturnOk() {
      webTestClient
          .get()
          .uri(
              uriBuilder ->
                  uriBuilder
                      .path("/validate-parameter/request-param")
                      .queryParam("query", "value")
                      .build())
          .exchange()
          .expectStatus()
          .isOk()
          .expectBody(String.class)
          .isEqualTo("OK");
    }

    /**
     * @see RequestHeaderController
     */
    @Test
    void givenValidRequestHeader_shouldReturnOk() {
      webTestClient
          .get()
          .uri("/validate-parameter/request-header")
          .header("X-Custom-Header", "value")
          .exchange()
          .expectStatus()
          .isOk()
          .expectBody(String.class)
          .isEqualTo("OK");
    }

    /**
     * @see CookieValueController
     */
    @Test
    void givenValidCookieValue_shouldReturnOk() {
      webTestClient
          .get()
          .uri("/validate-parameter/cookie-value")
          .cookie("x_session", "value")
          .exchange()
          .expectStatus()
          .isOk()
          .expectBody(String.class)
          .isEqualTo("OK");
    }

    /**
     * @see TwoArgController
     */
    @Test
    void givenBothParamsValid_shouldReturnOk() {
      webTestClient
          .get()
          .uri(
              uriBuilder ->
                  uriBuilder
                      .path("/validate-parameter/two-arg")
                      .queryParam("first", "validVal")
                      .queryParam("second", "anything")
                      .build())
          .exchange()
          .expectStatus()
          .isOk()
          .expectBody(String.class)
          .isEqualTo("OK");
    }

    /**
     * @see ThreeArgController
     */
    @Test
    void givenThreeParamsValid_shouldReturnOk() {
      webTestClient
          .get()
          .uri(
              uriBuilder ->
                  uriBuilder
                      .path("/validate-parameter/three-arg")
                      .queryParam("first", "anything")
                      .queryParam("second", "validVal")
                      .queryParam("third", "anything")
                      .build())
          .exchange()
          .expectStatus()
          .isOk()
          .expectBody(String.class)
          .isEqualTo("OK");
    }
  }

  @Nested
  @SpringBootTest(
      classes = {_TestApp.class},
      properties = "spring.validation.method.adapt-constraint-violations=false")
  @Import({
    PathVariableController.class,
    RequestParamController.class,
    RequestHeaderController.class,
    CookieValueController.class,
    MultiConstraintController.class,
    TwoArgController.class,
    ThreeArgController.class
  })
  @AutoConfigureWebTestClient
  class AdaptConstraintViolationFalse {

    @Autowired private WebTestClient webTestClient;

    /**
     * @see PathVariableController
     */
    @Test
    void givenTooShortPathVariable_shouldReturnValidationProblem() {
      webTestClient
          .get()
          .uri("/validate-parameter/path-variable/v")
          .exchange()
          .expectStatus()
          .isBadRequest()
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
                                  List.of(Map.of("field", "idVar", "error", VIOLATION_ERROR)))
                              .build()));
    }

    /**
     * @see RequestParamController
     */
    @Test
    void givenTooShortRequestParam_shouldReturnValidationProblem() {
      webTestClient
          .get()
          .uri(
              uriBuilder ->
                  uriBuilder
                      .path("/validate-parameter/request-param")
                      .queryParam("query", "v")
                      .build())
          .exchange()
          .expectStatus()
          .isBadRequest()
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
                                  List.of(Map.of("field", "queryParam", "error", VIOLATION_ERROR)))
                              .build()));
    }

    /**
     * @see RequestHeaderController
     */
    @Test
    void givenTooShortRequestHeader_shouldReturnValidationProblem() {
      webTestClient
          .get()
          .uri("/validate-parameter/request-header")
          .header("X-Custom-Header", "v")
          .exchange()
          .expectStatus()
          .isBadRequest()
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
                                  List.of(
                                      Map.of("field", "xCustomHeader", "error", VIOLATION_ERROR)))
                              .build()));
    }

    /**
     * @see CookieValueController
     */
    @Test
    void givenTooShortCookieValue_shouldReturnValidationProblem() {
      webTestClient
          .get()
          .uri("/validate-parameter/cookie-value")
          .cookie("x_session", "v")
          .exchange()
          .expectStatus()
          .isBadRequest()
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
                                  List.of(Map.of("field", "xSession", "error", VIOLATION_ERROR)))
                              .build()));
    }

    /**
     * @see MultiConstraintController
     */
    @Test
    void givenValueViolatingAllConstraints_shouldReturnAllErrors() {
      webTestClient
          .get()
          .uri(
              uriBuilder ->
                  uriBuilder
                      .path("/validate-parameter/multi-constraint")
                      .queryParam("input", "v")
                      .build())
          .exchange()
          .expectStatus()
          .isBadRequest()
          .expectHeader()
          .contentType(Problem.CONTENT_TYPE)
          .expectBody(Problem.class)
          .value(
              problem ->
                  assertThat(problem.getExtensionValue(ERRORS_EXTENSION))
                      .asInstanceOf(LIST)
                      .hasSize(2));
    }

    /**
     * @see MultiConstraintController
     */
    @ParameterizedTest
    @ValueSource(strings = {"vvvvv", "iiiii"})
    void givenValueViolatingSingleConstraint_shouldReturnCorrectError(String input) {
      webTestClient
          .get()
          .uri(
              uriBuilder ->
                  uriBuilder
                      .path("/validate-parameter/multi-constraint")
                      .queryParam("input", input)
                      .build())
          .exchange()
          .expectStatus()
          .isBadRequest()
          .expectHeader()
          .contentType(Problem.CONTENT_TYPE)
          .expectBody(Problem.class)
          .value(
              problem ->
                  assertThat(problem.getExtensionValue(ERRORS_EXTENSION))
                      .asInstanceOf(LIST)
                      .hasSize(1));
    }

    /**
     * @see TwoArgController
     */
    @Test
    void givenFirstParamTooShort_shouldReturnValidationError() {
      webTestClient
          .get()
          .uri(
              uriBuilder ->
                  uriBuilder
                      .path("/validate-parameter/two-arg")
                      .queryParam("first", "v")
                      .queryParam("second", "anything")
                      .build())
          .exchange()
          .expectStatus()
          .isBadRequest()
          .expectHeader()
          .contentType(Problem.CONTENT_TYPE)
          .expectBody(Problem.class)
          .value(
              problem ->
                  assertThat(problem.getExtensionValue(ERRORS_EXTENSION))
                      .asInstanceOf(LIST)
                      .hasSize(1)
                      .allSatisfy(
                          e -> assertThat(((Map<?, ?>) e).get("field")).isEqualTo("firstParam")));
    }

    /**
     * @see ThreeArgController
     */
    @Test
    void givenSecondParamTooShort_shouldReturnValidationError() {
      webTestClient
          .get()
          .uri(
              uriBuilder ->
                  uriBuilder
                      .path("/validate-parameter/three-arg")
                      .queryParam("first", "anything")
                      .queryParam("second", "v")
                      .queryParam("third", "anything")
                      .build())
          .exchange()
          .expectStatus()
          .isBadRequest()
          .expectHeader()
          .contentType(Problem.CONTENT_TYPE)
          .expectBody(Problem.class)
          .value(
              problem ->
                  assertThat(problem.getExtensionValue(ERRORS_EXTENSION))
                      .asInstanceOf(LIST)
                      .hasSize(1)
                      .allSatisfy(
                          e -> assertThat(((Map<?, ?>) e).get("field")).isEqualTo("secondParam")));
    }

    /**
     * @see TwoArgController
     */
    @Test
    void givenBothParamsValid_shouldReturnOk() {
      webTestClient
          .get()
          .uri(
              uriBuilder ->
                  uriBuilder
                      .path("/validate-parameter/two-arg")
                      .queryParam("first", "validVal")
                      .queryParam("second", "anything")
                      .build())
          .exchange()
          .expectStatus()
          .isOk()
          .expectBody(String.class)
          .isEqualTo("OK");
    }
  }

  @Nested
  @SpringBootTest(
      classes = {_TestApp.class},
      properties = "spring.validation.method.adapt-constraint-violations=true")
  @Import({
    PathVariableController.class,
    RequestParamController.class,
    RequestHeaderController.class,
    CookieValueController.class,
    MultiConstraintController.class,
    TwoArgController.class,
    ThreeArgController.class
  })
  @AutoConfigureWebTestClient
  class AdaptConstraintViolationTrue {

    @Autowired private WebTestClient webTestClient;

    /**
     * @see PathVariableController
     */
    @Test
    void givenTooShortPathVariable_shouldReturnValidationProblem() {
      webTestClient
          .get()
          .uri("/validate-parameter/path-variable/v")
          .exchange()
          .expectStatus()
          .isBadRequest()
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
                                  List.of(Map.of("field", "id", "error", VIOLATION_ERROR)))
                              .build()));
    }

    /**
     * @see RequestParamController
     */
    @Test
    void givenTooShortRequestParam_shouldReturnValidationProblem() {
      webTestClient
          .get()
          .uri(
              uriBuilder ->
                  uriBuilder
                      .path("/validate-parameter/request-param")
                      .queryParam("query", "v")
                      .build())
          .exchange()
          .expectStatus()
          .isBadRequest()
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
                                  List.of(Map.of("field", "query", "error", VIOLATION_ERROR)))
                              .build()));
    }

    /**
     * @see RequestHeaderController
     */
    @Test
    void givenTooShortRequestHeader_shouldReturnValidationProblem() {
      webTestClient
          .get()
          .uri("/validate-parameter/request-header")
          .header("X-Custom-Header", "v")
          .exchange()
          .expectStatus()
          .isBadRequest()
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
                                  List.of(
                                      Map.of("field", "X-Custom-Header", "error", VIOLATION_ERROR)))
                              .build()));
    }

    /**
     * @see CookieValueController
     */
    @Test
    void givenTooShortCookieValue_shouldReturnValidationProblem() {
      webTestClient
          .get()
          .uri("/validate-parameter/cookie-value")
          .cookie("x_session", "v")
          .exchange()
          .expectStatus()
          .isBadRequest()
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
                                  List.of(Map.of("field", "x_session", "error", VIOLATION_ERROR)))
                              .build()));
    }

    /**
     * @see MultiConstraintController
     */
    @Test
    void givenValueViolatingAllConstraints_shouldReturnAllErrors() {
      webTestClient
          .get()
          .uri(
              uriBuilder ->
                  uriBuilder
                      .path("/validate-parameter/multi-constraint")
                      .queryParam("input", "v")
                      .build())
          .exchange()
          .expectStatus()
          .isBadRequest()
          .expectHeader()
          .contentType(Problem.CONTENT_TYPE)
          .expectBody(Problem.class)
          .value(
              problem ->
                  assertThat(problem.getExtensionValue(ERRORS_EXTENSION))
                      .asInstanceOf(LIST)
                      .hasSize(2));
    }

    /**
     * @see MultiConstraintController
     */
    @ParameterizedTest
    @ValueSource(strings = {"vvvvv", "iiiii"})
    void givenValueViolatingSingleConstraint_shouldReturnCorrectError(String input) {
      webTestClient
          .get()
          .uri(
              uriBuilder ->
                  uriBuilder
                      .path("/validate-parameter/multi-constraint")
                      .queryParam("input", input)
                      .build())
          .exchange()
          .expectStatus()
          .isBadRequest()
          .expectHeader()
          .contentType(Problem.CONTENT_TYPE)
          .expectBody(Problem.class)
          .value(
              problem ->
                  assertThat(problem.getExtensionValue(ERRORS_EXTENSION))
                      .asInstanceOf(LIST)
                      .hasSize(1));
    }

    /**
     * @see TwoArgController
     */
    @Test
    void givenFirstParamTooShort_shouldReturnValidationError() {
      webTestClient
          .get()
          .uri(
              uriBuilder ->
                  uriBuilder
                      .path("/validate-parameter/two-arg")
                      .queryParam("first", "v")
                      .queryParam("second", "anything")
                      .build())
          .exchange()
          .expectStatus()
          .isBadRequest()
          .expectHeader()
          .contentType(Problem.CONTENT_TYPE)
          .expectBody(Problem.class)
          .value(
              problem ->
                  assertThat(problem.getExtensionValue(ERRORS_EXTENSION))
                      .asInstanceOf(LIST)
                      .hasSize(1)
                      .allSatisfy(
                          e -> assertThat(((Map<?, ?>) e).get("field")).isEqualTo("first")));
    }

    /**
     * @see ThreeArgController
     */
    @Test
    void givenSecondParamTooShort_shouldReturnValidationError() {
      webTestClient
          .get()
          .uri(
              uriBuilder ->
                  uriBuilder
                      .path("/validate-parameter/three-arg")
                      .queryParam("first", "anything")
                      .queryParam("second", "v")
                      .queryParam("third", "anything")
                      .build())
          .exchange()
          .expectStatus()
          .isBadRequest()
          .expectHeader()
          .contentType(Problem.CONTENT_TYPE)
          .expectBody(Problem.class)
          .value(
              problem ->
                  assertThat(problem.getExtensionValue(ERRORS_EXTENSION))
                      .asInstanceOf(LIST)
                      .hasSize(1)
                      .allSatisfy(
                          e -> assertThat(((Map<?, ?>) e).get("field")).isEqualTo("second")));
    }
  }
}
