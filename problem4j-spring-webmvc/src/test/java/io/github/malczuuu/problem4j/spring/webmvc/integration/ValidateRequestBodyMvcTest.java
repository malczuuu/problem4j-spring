package io.github.malczuuu.problem4j.spring.webmvc.integration;

import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.ERRORS_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.VALIDATION_FAILED_DETAIL;
import static io.github.malczuuu.problem4j.spring.webmvc.integration.ValidateRequestBodyMvcTest.ValidateRequestBodyController;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.webmvc.app.MvcTestApp;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest(classes = {MvcTestApp.class})
@Import({ValidateRequestBodyController.class})
@AutoConfigureMockMvc
class ValidateRequestBodyMvcTest {

  @RestController
  static class ValidateRequestBodyController {

    @PostMapping(path = "/validate-request-body")
    String validateRequestBody(@Valid @RequestBody TestRequest request) {
      return "OK";
    }

    @PostMapping(path = "/validate-global-object")
    String validateGlobalObject(@Valid @RequestBody AlwaysInvalidRequest body) {
      return "OK";
    }
  }

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void givenInvalidRequestBody_shouldReturnProblem() throws Exception {
    TestRequest invalidRequest = new TestRequest("", null);

    mockMvc
        .perform(
            post("/validate-request-body")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertThat(result.getResolvedException())
                    .isInstanceOf(MethodArgumentNotValidException.class))
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
                              List.of(Map.of("field", "name", "error", "must not be blank")))
                          .build());
            });
  }

  @Test
  void givenGlobalValidationViolation_shouldReturnProblemWithoutFieldName() throws Exception {
    String json = "{\"field\":\"value\"}";

    mockMvc
        .perform(
            post("/validate-global-object").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertThat(result.getResolvedException())
                    .isInstanceOf(MethodArgumentNotValidException.class))
        .andExpect(content().contentType(Problem.CONTENT_TYPE))
        .andExpect(
            result -> {
              Problem problem =
                  objectMapper.readValue(result.getResponse().getContentAsString(), Problem.class);
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
            });
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"{ \"name\": \"Alice\"", "{ \"name\": \"Alice\", \"age\": \"too young\"}", ""})
  @NullSource
  void givenMalformedRequestBody_shouldReturnProblem(String json) throws Exception {
    MockHttpServletRequestBuilder builder =
        post("/validate-request-body").contentType(MediaType.APPLICATION_JSON);
    if (json != null) {
      builder.content(json);
    }

    mockMvc
        .perform(builder)
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertThat(result.getResolvedException())
                    .isInstanceOf(HttpMessageNotReadableException.class))
        .andExpect(content().contentType(Problem.CONTENT_TYPE))
        .andExpect(
            result -> {
              Problem problem =
                  objectMapper.readValue(result.getResponse().getContentAsString(), Problem.class);
              assertThat(problem)
                  .isEqualTo(Problem.builder().status(ProblemStatus.BAD_REQUEST).build());
            });
  }

  record TestRequest(@NotBlank String name, Integer age) {}

  @AlwaysInvalid
  record AlwaysInvalidRequest(String field) {}

  @Documented
  @Constraint(validatedBy = AlwaysInvalidValidator.class)
  @Target(TYPE)
  @Retention(RUNTIME)
  @interface AlwaysInvalid {

    String message() default "always invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
  }

  static class AlwaysInvalidValidator implements ConstraintValidator<AlwaysInvalid, Object> {
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
      return false;
    }
  }
}
