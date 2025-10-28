package io.github.malczuuu.problem4j.spring.webflux.integration;

import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.ERRORS_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.VALIDATION_FAILED_DETAIL;
import static io.github.malczuuu.problem4j.spring.webflux.integration.ValidateMethodArgumentFailingWebFluxTest.ValidateParameterController;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;
import static org.hamcrest.Matchers.notNullValue;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.webflux.app.WebFluxTestApp;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Map;
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

@SpringBootTest(
    classes = {WebFluxTestApp.class},
    properties = {"spring.validation.method.adapt-constraint-violations=false"},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({ValidateParameterController.class})
@AutoConfigureWebTestClient
class ValidateMethodArgumentFailingWebFluxTest {

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

  @Autowired private WebTestClient webTestClient;

  /**
   * @see ValidateParameterController#validatePathVariable(String)
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
        .value(notNullValue())
        .isEqualTo(
            Problem.builder()
                .status(ProblemStatus.BAD_REQUEST)
                .detail(VALIDATION_FAILED_DETAIL)
                .extension(
                    ERRORS_EXTENSION, List.of(Map.of("field", "idVar", "error", VIOLATION_ERROR)))
                .build());
  }

  /**
   * @see ValidateParameterController#validateRequestParam(String)
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
        .value(notNullValue())
        .isEqualTo(
            Problem.builder()
                .status(ProblemStatus.BAD_REQUEST)
                .detail(VALIDATION_FAILED_DETAIL)
                .extension(
                    ERRORS_EXTENSION,
                    List.of(Map.of("field", "queryParam", "error", VIOLATION_ERROR)))
                .build());
  }

  /**
   * @see ValidateParameterController#validateRequestHeader(String)
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
        .value(notNullValue())
        .isEqualTo(
            Problem.builder()
                .status(ProblemStatus.BAD_REQUEST)
                .detail(VALIDATION_FAILED_DETAIL)
                .extension(
                    ERRORS_EXTENSION,
                    List.of(Map.of("field", "xCustomHeader", "error", VIOLATION_ERROR)))
                .build());
  }

  /**
   * @see ValidateParameterController#validateCookieValue(String)
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
        .value(notNullValue())
        .value(notNullValue())
        .isEqualTo(
            Problem.builder()
                .status(ProblemStatus.BAD_REQUEST)
                .detail(VALIDATION_FAILED_DETAIL)
                .extension(
                    ERRORS_EXTENSION,
                    List.of(Map.of("field", "xSession", "error", VIOLATION_ERROR)))
                .build());
  }

  /**
   * @see ValidateParameterController#validateMultiConstraint(String)
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
        .value(notNullValue())
        .value(
            problem ->
                assertThat(problem.getExtensionValue(ERRORS_EXTENSION))
                    .asInstanceOf(LIST)
                    .hasSize(2));
  }

  /**
   * @see ValidateParameterController#validateMultiConstraint(String)
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
        .value(notNullValue())
        .value(
            problem ->
                assertThat(problem.getExtensionValue(ERRORS_EXTENSION))
                    .asInstanceOf(LIST)
                    .hasSize(1));
  }

  /**
   * @see ValidateParameterController#validateTwoArguments(String, String)
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
        .value(notNullValue())
        .value(
            problem ->
                assertThat(problem.getExtensionValue(ERRORS_EXTENSION))
                    .asInstanceOf(LIST)
                    .hasSize(1)
                    .allSatisfy(
                        e -> assertThat(((Map<?, ?>) e).get("field")).isEqualTo("firstParam")));
  }

  /**
   * @see ValidateParameterController#validateThreeArguments(String, String, String)
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
        .value(notNullValue())
        .value(
            problem ->
                assertThat(problem.getExtensionValue(ERRORS_EXTENSION))
                    .asInstanceOf(LIST)
                    .hasSize(1)
                    .allSatisfy(
                        e -> assertThat(((Map<?, ?>) e).get("field")).isEqualTo("secondParam")));
  }

  /**
   * @see ValidateParameterController#validateTwoArguments(String, String)
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
        .value(notNullValue())
        .isEqualTo("OK");
  }
}
