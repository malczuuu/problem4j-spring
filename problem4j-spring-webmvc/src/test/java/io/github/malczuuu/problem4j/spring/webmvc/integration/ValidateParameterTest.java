package io.github.malczuuu.problem4j.spring.webmvc.integration;

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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.method.MethodValidationException;
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
    @GetMapping(path = "/validate-parameter/path-variable/{id}")
    String endpoint(@PathVariable("id") @Size(min = 5) String idVar) {
      return "OK";
    }
  }

  @Validated
  @RestController
  static class RequestParamController {
    @GetMapping(path = "/validate-parameter/request-param")
    String endpoint(@RequestParam("query") @Size(min = 5) String queryParam) {
      return "OK";
    }
  }

  @Validated
  @RestController
  static class RequestHeaderController {
    @GetMapping(path = "/validate-parameter/request-header")
    String endpoint(@RequestHeader("X-Custom-Header") @Size(min = 5) String xCustomHeader) {
      return "OK";
    }
  }

  @Validated
  @RestController
  static class CookieValueController {
    @GetMapping(path = "/validate-parameter/cookie-value")
    String endpoint(@CookieValue("x_session") @Size(min = 5) String xSession) {
      return "OK";
    }
  }

  @Validated
  @RestController
  static class MultiConstraintController {
    @GetMapping(path = "/validate-parameter/multi-constraint")
    String endpoint(
        @RequestParam("input") @Size(min = 5) @Pattern(regexp = "i") String inputParam) {
      return "OK";
    }
  }

  @Validated
  @RestController
  static class TwoArgController {
    @GetMapping(path = "/validate-parameter/two-arg")
    String endpoint(
        @RequestParam("first") @Size(min = 5) String firstParam,
        @RequestParam("second") String secondParam) {
      return "OK";
    }
  }

  @Validated
  @RestController
  static class ThreeArgController {
    @GetMapping(path = "/validate-parameter/three-arg")
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
  @AutoConfigureMockMvc
  class ValidationPassed {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    /**
     * @see PathVariableController
     */
    @Test
    void givenValidPathVariable_shouldReturnOk() throws Exception {
      mockMvc
          .perform(get("/validate-parameter/path-variable/value"))
          .andExpect(status().isOk())
          .andExpect(content().string("OK"));
    }

    /**
     * @see RequestParamController
     */
    @Test
    void givenValidRequestParam_shouldReturnOk() throws Exception {
      mockMvc
          .perform(get("/validate-parameter/request-param").param("query", "value"))
          .andExpect(status().isOk())
          .andExpect(content().string("OK"));
    }

    /**
     * @see RequestHeaderController
     */
    @Test
    void givenValidRequestHeader_shouldReturnOk() throws Exception {
      mockMvc
          .perform(get("/validate-parameter/request-header").header("X-Custom-Header", "value"))
          .andExpect(status().isOk())
          .andExpect(content().string("OK"));
    }

    /**
     * @see CookieValueController
     */
    @Test
    void givenValidCookieValue_shouldReturnOk() throws Exception {
      mockMvc
          .perform(get("/validate-parameter/cookie-value").cookie(new Cookie("x_session", "value")))
          .andExpect(status().isOk())
          .andExpect(content().string("OK"));
    }

    /**
     * @see TwoArgController
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

    /**
     * @see ThreeArgController
     */
    @Test
    void givenThreeParamsValid_shouldReturnOk() throws Exception {
      mockMvc
          .perform(
              get("/validate-parameter/three-arg")
                  .param("first", "anything")
                  .param("second", "validVal")
                  .param("third", "anything"))
          .andExpect(status().isOk())
          .andExpect(content().string("OK"));
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
  @AutoConfigureMockMvc
  class AdaptConstraintViolationFalse {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    /**
     * @see PathVariableController
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
                    objectMapper.readValue(
                        result.getResponse().getContentAsString(), Problem.class);
                assertThat(problem)
                    .isEqualTo(
                        Problem.builder()
                            .status(ProblemStatus.BAD_REQUEST)
                            .detail("Validation failed")
                            .extension(
                                "errors",
                                List.of(Map.of("field", "idVar", "error", VIOLATION_ERROR)))
                            .build());
              });
    }

    /**
     * @see RequestParamController
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
                    objectMapper.readValue(
                        result.getResponse().getContentAsString(), Problem.class);
                assertThat(problem)
                    .isEqualTo(
                        Problem.builder()
                            .status(ProblemStatus.BAD_REQUEST)
                            .detail("Validation failed")
                            .extension(
                                "errors",
                                List.of(Map.of("field", "queryParam", "error", VIOLATION_ERROR)))
                            .build());
              });
    }

    /**
     * @see RequestHeaderController
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
                    objectMapper.readValue(
                        result.getResponse().getContentAsString(), Problem.class);
                assertThat(problem)
                    .isEqualTo(
                        Problem.builder()
                            .status(ProblemStatus.BAD_REQUEST)
                            .detail("Validation failed")
                            .extension(
                                "errors",
                                List.of(Map.of("field", "xCustomHeader", "error", VIOLATION_ERROR)))
                            .build());
              });
    }

    /**
     * @see CookieValueController
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
                    objectMapper.readValue(
                        result.getResponse().getContentAsString(), Problem.class);
                assertThat(problem)
                    .isEqualTo(
                        Problem.builder()
                            .status(ProblemStatus.BAD_REQUEST)
                            .detail("Validation failed")
                            .extension(
                                "errors",
                                List.of(Map.of("field", "xSession", "error", VIOLATION_ERROR)))
                            .build());
              });
    }

    /**
     * @see MultiConstraintController
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
                    objectMapper.readValue(
                        result.getResponse().getContentAsString(), Problem.class);
                assertThat(problem.getExtensionValue("errors")).asInstanceOf(LIST).hasSize(2);
              });
    }

    /**
     * @see MultiConstraintController
     */
    @ParameterizedTest
    @ValueSource(strings = {"vvvvv", "iiiii"})
    void givenValueViolatingSingleConstraint_shouldReturnCorrectError(String input)
        throws Exception {
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
                    objectMapper.readValue(
                        result.getResponse().getContentAsString(), Problem.class);
                assertThat(problem.getExtensionValue("errors")).asInstanceOf(LIST).hasSize(1);
              });
    }

    /**
     * @see TwoArgController
     */
    @Test
    void givenFirstParamTooShort_shouldReturnValidationError() throws Exception {
      mockMvc
          .perform(
              get("/validate-parameter/two-arg").param("first", "v").param("second", "anything"))
          .andExpect(status().isBadRequest())
          .andExpect(
              result ->
                  assertThat(result.getResolvedException())
                      .isInstanceOf(ConstraintViolationException.class))
          .andExpect(content().contentType(Problem.CONTENT_TYPE))
          .andExpect(
              result -> {
                Problem problem =
                    objectMapper.readValue(
                        result.getResponse().getContentAsString(), Problem.class);
                assertThat(problem.getExtensionValue("errors"))
                    .asInstanceOf(LIST)
                    .hasSize(1)
                    .allSatisfy(
                        e -> assertThat(((Map<?, ?>) e).get("field")).isEqualTo("firstParam"));
              });
    }

    /**
     * @see ThreeArgController
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
                    objectMapper.readValue(
                        result.getResponse().getContentAsString(), Problem.class);
                assertThat(problem.getExtensionValue("errors"))
                    .asInstanceOf(LIST)
                    .hasSize(1)
                    .allSatisfy(
                        e -> assertThat(((Map<?, ?>) e).get("field")).isEqualTo("secondParam"));
              });
    }

    /**
     * @see TwoArgController
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
  @AutoConfigureMockMvc
  class AdaptConstraintViolationTrue {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    /**
     * @see PathVariableController
     */
    @Test
    void givenTooShortPathVariable_shouldReturnValidationProblem() throws Exception {
      mockMvc
          .perform(get("/validate-parameter/path-variable/v"))
          .andExpect(status().isBadRequest())
          .andExpect(
              result ->
                  assertThat(result.getResolvedException())
                      .isInstanceOf(MethodValidationException.class))
          .andExpect(content().contentType(Problem.CONTENT_TYPE))
          .andExpect(
              result -> {
                Problem problem =
                    objectMapper.readValue(
                        result.getResponse().getContentAsString(), Problem.class);
                assertThat(problem)
                    .isEqualTo(
                        Problem.builder()
                            .status(ProblemStatus.BAD_REQUEST)
                            .detail("Validation failed")
                            .extension(
                                "errors", List.of(Map.of("field", "id", "error", VIOLATION_ERROR)))
                            .build());
              });
    }

    /**
     * @see RequestParamController
     */
    @Test
    void givenTooShortRequestParam_shouldReturnValidationProblem() throws Exception {
      mockMvc
          .perform(get("/validate-parameter/request-param").param("query", "v"))
          .andExpect(status().isBadRequest())
          .andExpect(
              result ->
                  assertThat(result.getResolvedException())
                      .isInstanceOf(MethodValidationException.class))
          .andExpect(content().contentType(Problem.CONTENT_TYPE))
          .andExpect(
              result -> {
                Problem problem =
                    objectMapper.readValue(
                        result.getResponse().getContentAsString(), Problem.class);
                assertThat(problem)
                    .isEqualTo(
                        Problem.builder()
                            .status(ProblemStatus.BAD_REQUEST)
                            .detail("Validation failed")
                            .extension(
                                "errors",
                                List.of(Map.of("field", "query", "error", VIOLATION_ERROR)))
                            .build());
              });
    }

    /**
     * @see RequestHeaderController
     */
    @Test
    void givenTooShortRequestHeader_shouldReturnValidationProblem() throws Exception {
      mockMvc
          .perform(get("/validate-parameter/request-header").header("X-Custom-Header", "v"))
          .andExpect(status().isBadRequest())
          .andExpect(
              result ->
                  assertThat(result.getResolvedException())
                      .isInstanceOf(MethodValidationException.class))
          .andExpect(content().contentType(Problem.CONTENT_TYPE))
          .andExpect(
              result -> {
                Problem problem =
                    objectMapper.readValue(
                        result.getResponse().getContentAsString(), Problem.class);
                assertThat(problem)
                    .isEqualTo(
                        Problem.builder()
                            .status(ProblemStatus.BAD_REQUEST)
                            .detail("Validation failed")
                            .extension(
                                "errors",
                                List.of(
                                    Map.of("field", "X-Custom-Header", "error", VIOLATION_ERROR)))
                            .build());
              });
    }

    /**
     * @see CookieValueController
     */
    @Test
    void givenTooShortCookieValue_shouldReturnValidationProblem() throws Exception {
      mockMvc
          .perform(get("/validate-parameter/cookie-value").cookie(new Cookie("x_session", "v")))
          .andExpect(status().isBadRequest())
          .andExpect(
              result ->
                  assertThat(result.getResolvedException())
                      .isInstanceOf(MethodValidationException.class))
          .andExpect(content().contentType(Problem.CONTENT_TYPE))
          .andExpect(
              result -> {
                Problem problem =
                    objectMapper.readValue(
                        result.getResponse().getContentAsString(), Problem.class);
                assertThat(problem)
                    .isEqualTo(
                        Problem.builder()
                            .status(ProblemStatus.BAD_REQUEST)
                            .detail("Validation failed")
                            .extension(
                                "errors",
                                List.of(Map.of("field", "x_session", "error", VIOLATION_ERROR)))
                            .build());
              });
    }

    /**
     * @see MultiConstraintController
     */
    @Test
    void givenValueViolatingAllConstraints_shouldReturnAllErrors() throws Exception {
      mockMvc
          .perform(get("/validate-parameter/multi-constraint").param("input", "v"))
          .andExpect(status().isBadRequest())
          .andExpect(
              result ->
                  assertThat(result.getResolvedException())
                      .isInstanceOf(MethodValidationException.class))
          .andExpect(content().contentType(Problem.CONTENT_TYPE))
          .andExpect(
              result -> {
                Problem problem =
                    objectMapper.readValue(
                        result.getResponse().getContentAsString(), Problem.class);
                assertThat(problem.getExtensionValue("errors")).asInstanceOf(LIST).hasSize(2);
              });
    }

    /**
     * @see MultiConstraintController
     */
    @ParameterizedTest
    @ValueSource(strings = {"vvvvv", "iiiii"})
    void givenValueViolatingSingleConstraint_shouldReturnCorrectError(String input)
        throws Exception {
      mockMvc
          .perform(get("/validate-parameter/multi-constraint").param("input", input))
          .andExpect(status().isBadRequest())
          .andExpect(
              result ->
                  assertThat(result.getResolvedException())
                      .isInstanceOf(MethodValidationException.class))
          .andExpect(content().contentType(Problem.CONTENT_TYPE))
          .andExpect(
              result -> {
                Problem problem =
                    objectMapper.readValue(
                        result.getResponse().getContentAsString(), Problem.class);
                assertThat(problem.getExtensionValue("errors")).asInstanceOf(LIST).hasSize(1);
              });
    }

    /**
     * @see TwoArgController
     */
    @Test
    void givenFirstParamTooShort_shouldReturnValidationError() throws Exception {
      mockMvc
          .perform(
              get("/validate-parameter/two-arg").param("first", "v").param("second", "anything"))
          .andExpect(status().isBadRequest())
          .andExpect(
              result ->
                  assertThat(result.getResolvedException())
                      .isInstanceOf(MethodValidationException.class))
          .andExpect(content().contentType(Problem.CONTENT_TYPE))
          .andExpect(
              result -> {
                Problem problem =
                    objectMapper.readValue(
                        result.getResponse().getContentAsString(), Problem.class);
                assertThat(problem.getExtensionValue("errors"))
                    .asInstanceOf(LIST)
                    .hasSize(1)
                    .allSatisfy(e -> assertThat(((Map<?, ?>) e).get("field")).isEqualTo("first"));
              });
    }

    /**
     * @see ThreeArgController
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
                      .isInstanceOf(MethodValidationException.class))
          .andExpect(content().contentType(Problem.CONTENT_TYPE))
          .andExpect(
              result -> {
                Problem problem =
                    objectMapper.readValue(
                        result.getResponse().getContentAsString(), Problem.class);
                assertThat(problem.getExtensionValue("errors"))
                    .asInstanceOf(LIST)
                    .hasSize(1)
                    .allSatisfy(e -> assertThat(((Map<?, ?>) e).get("field")).isEqualTo("second"));
              });
    }
  }
}
