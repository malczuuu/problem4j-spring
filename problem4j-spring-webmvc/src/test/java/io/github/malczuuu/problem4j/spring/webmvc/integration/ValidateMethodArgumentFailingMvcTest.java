package io.github.malczuuu.problem4j.spring.webmvc.integration;

import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.ERRORS_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.VALIDATION_FAILED_DETAIL;
import static io.github.malczuuu.problem4j.spring.webmvc.integration.ValidateMethodArgumentFailingMvcTest.ValidateParameterController;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import jakarta.servlet.http.Cookie;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest(
    classes = {_TestApp.class},
    properties = "spring.validation.method.adapt-constraint-violations=false")
@Import({ValidateParameterController.class})
@AutoConfigureMockMvc
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

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  /**
   * @see ValidateParameterController#validatePathVariable(String)
   */
  @Test
  void givenTooShortPathVariable_shouldReturnValidationProblem() throws Exception {
    mockMvc
        .perform(get("/validate-parameter/path-variable/v"))
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertThat(result.getResolvedException())
                    .isInstanceOf(ConstraintViolationException.class))
        .andExpect(content().contentType(Problem.CONTENT_TYPE))
        .andExpect(
            result -> {
              Problem problem =
                  objectMapper.readValue(result.getResponse().getContentAsString(), Problem.class);
              assertThat(problem)
                  .isEqualTo(
                      Problem.builder()
                          .status(ProblemStatus.BAD_REQUEST)
                          .detail(VALIDATION_FAILED_DETAIL)
                          .extension(
                              ERRORS_EXTENSION,
                              List.of(Map.of("field", "idVar", "error", VIOLATION_ERROR)))
                          .build());
            });
  }

  /**
   * @see ValidateParameterController#validateRequestParam(String)
   */
  @Test
  void givenTooShortRequestParam_shouldReturnValidationProblem() throws Exception {
    mockMvc
        .perform(get("/validate-parameter/request-param").param("query", "v"))
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertThat(result.getResolvedException())
                    .isInstanceOf(ConstraintViolationException.class))
        .andExpect(content().contentType(Problem.CONTENT_TYPE))
        .andExpect(
            result -> {
              Problem problem =
                  objectMapper.readValue(result.getResponse().getContentAsString(), Problem.class);
              assertThat(problem)
                  .isEqualTo(
                      Problem.builder()
                          .status(ProblemStatus.BAD_REQUEST)
                          .detail(VALIDATION_FAILED_DETAIL)
                          .extension(
                              ERRORS_EXTENSION,
                              List.of(Map.of("field", "queryParam", "error", VIOLATION_ERROR)))
                          .build());
            });
  }

  /**
   * @see ValidateParameterController#validateRequestHeader(String)
   */
  @Test
  void givenTooShortRequestHeader_shouldReturnValidationProblem() throws Exception {
    mockMvc
        .perform(get("/validate-parameter/request-header").header("X-Custom-Header", "v"))
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertThat(result.getResolvedException())
                    .isInstanceOf(ConstraintViolationException.class))
        .andExpect(content().contentType(Problem.CONTENT_TYPE))
        .andExpect(
            result -> {
              Problem problem =
                  objectMapper.readValue(result.getResponse().getContentAsString(), Problem.class);
              assertThat(problem)
                  .isEqualTo(
                      Problem.builder()
                          .status(ProblemStatus.BAD_REQUEST)
                          .detail(VALIDATION_FAILED_DETAIL)
                          .extension(
                              ERRORS_EXTENSION,
                              List.of(Map.of("field", "xCustomHeader", "error", VIOLATION_ERROR)))
                          .build());
            });
  }

  /**
   * @see ValidateParameterController#validateCookieValue(String)
   */
  @Test
  void givenTooShortCookieValue_shouldReturnValidationProblem() throws Exception {
    mockMvc
        .perform(get("/validate-parameter/cookie-value").cookie(new Cookie("x_session", "v")))
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertThat(result.getResolvedException())
                    .isInstanceOf(ConstraintViolationException.class))
        .andExpect(content().contentType(Problem.CONTENT_TYPE))
        .andExpect(
            result -> {
              Problem problem =
                  objectMapper.readValue(result.getResponse().getContentAsString(), Problem.class);
              assertThat(problem)
                  .isEqualTo(
                      Problem.builder()
                          .status(ProblemStatus.BAD_REQUEST)
                          .detail(VALIDATION_FAILED_DETAIL)
                          .extension(
                              ERRORS_EXTENSION,
                              List.of(Map.of("field", "xSession", "error", VIOLATION_ERROR)))
                          .build());
            });
  }

  /**
   * @see ValidateParameterController#validateMultiConstraint(String)
   */
  @Test
  void givenValueViolatingAllConstraints_shouldReturnAllErrors() throws Exception {
    mockMvc
        .perform(get("/validate-parameter/multi-constraint").param("input", "v"))
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertThat(result.getResolvedException())
                    .isInstanceOf(ConstraintViolationException.class))
        .andExpect(content().contentType(Problem.CONTENT_TYPE))
        .andExpect(
            result -> {
              Problem problem =
                  objectMapper.readValue(result.getResponse().getContentAsString(), Problem.class);
              assertThat(problem.getExtensionValue(ERRORS_EXTENSION)).asInstanceOf(LIST).hasSize(2);
            });
  }

  /**
   * @see ValidateParameterController#validateMultiConstraint(String)
   */
  @ParameterizedTest
  @ValueSource(strings = {"vvvvv", "iiiii"})
  void givenValueViolatingSingleConstraint_shouldReturnCorrectError(String input) throws Exception {
    mockMvc
        .perform(get("/validate-parameter/multi-constraint").param("input", input))
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertThat(result.getResolvedException())
                    .isInstanceOf(ConstraintViolationException.class))
        .andExpect(content().contentType(Problem.CONTENT_TYPE))
        .andExpect(
            result -> {
              Problem problem =
                  objectMapper.readValue(result.getResponse().getContentAsString(), Problem.class);
              assertThat(problem.getExtensionValue(ERRORS_EXTENSION)).asInstanceOf(LIST).hasSize(1);
            });
  }

  /**
   * @see ValidateParameterController#validateTwoArguments(String, String)
   */
  @Test
  void givenFirstParamTooShort_shouldReturnValidationError() throws Exception {
    mockMvc
        .perform(get("/validate-parameter/two-arg").param("first", "v").param("second", "anything"))
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertThat(result.getResolvedException())
                    .isInstanceOf(ConstraintViolationException.class))
        .andExpect(content().contentType(Problem.CONTENT_TYPE))
        .andExpect(
            result -> {
              Problem problem =
                  objectMapper.readValue(result.getResponse().getContentAsString(), Problem.class);
              assertThat(problem.getExtensionValue(ERRORS_EXTENSION))
                  .asInstanceOf(LIST)
                  .hasSize(1)
                  .allSatisfy(
                      e -> assertThat(((Map<?, ?>) e).get("field")).isEqualTo("firstParam"));
            });
  }

  /**
   * @see ValidateParameterController#validateThreeArguments(String, String, String)
   */
  @Test
  void givenSecondParamTooShort_shouldReturnValidationError() throws Exception {
    mockMvc
        .perform(
            get("/validate-parameter/three-arg")
                .param("first", "anything")
                .param("second", "v")
                .param("third", "anything"))
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertThat(result.getResolvedException())
                    .isInstanceOf(ConstraintViolationException.class))
        .andExpect(content().contentType(Problem.CONTENT_TYPE))
        .andExpect(
            result -> {
              Problem problem =
                  objectMapper.readValue(result.getResponse().getContentAsString(), Problem.class);
              assertThat(problem.getExtensionValue(ERRORS_EXTENSION))
                  .asInstanceOf(LIST)
                  .hasSize(1)
                  .allSatisfy(
                      e -> assertThat(((Map<?, ?>) e).get("field")).isEqualTo("secondParam"));
            });
  }

  /**
   * @see ValidateParameterController#validateTwoArguments(String, String)
   */
  @Test
  void givenBothParamsValid_shouldReturnOk() throws Exception {
    mockMvc
        .perform(
            get("/validate-parameter/two-arg")
                .param("first", "validVal")
                .param("second", "anything"))
        .andExpect(status().isOk())
        .andExpect(content().string("OK"));
  }
}
