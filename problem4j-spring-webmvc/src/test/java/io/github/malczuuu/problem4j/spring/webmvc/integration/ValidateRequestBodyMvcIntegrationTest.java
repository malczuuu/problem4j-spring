package io.github.malczuuu.problem4j.spring.webmvc.integration;

import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.ERRORS_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.VALIDATION_FAILED_DETAIL;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest(
    classes = {MvcTestApp.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({ValidateRequestBodyMvcIntegrationTest.ValidateRequestBodyController.class})
class ValidateRequestBodyMvcIntegrationTest {

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

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private ObjectMapper objectMapper;

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

  @Test
  void givenInvalidRequestBody_shouldReturnProblem() throws Exception {
    TestRequest invalidRequest = new TestRequest("", null);

    ResponseEntity<String> response =
        restTemplate.postForEntity("/validate-request-body", invalidRequest, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getHeaders().getContentType()).hasToString(Problem.CONTENT_TYPE);

    Problem problem = objectMapper.readValue(response.getBody(), Problem.class);

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
  void givenGlobalValidationViolation_shouldReturnProblemWithoutFieldName() throws Exception {
    AlwaysInvalidRequest body = new AlwaysInvalidRequest("value");

    ResponseEntity<String> response =
        restTemplate.postForEntity("/validate-global-object", body, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getHeaders().getContentType()).hasToString(Problem.CONTENT_TYPE);

    Problem problem = objectMapper.readValue(response.getBody(), Problem.class);

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
  void givenMalformedRequestBody_shouldReturnProblem(String json) throws JsonProcessingException {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<String> entity = new HttpEntity<>(json, headers);

    ResponseEntity<String> response =
        restTemplate.postForEntity("/validate-request-body", entity, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getHeaders().getContentType()).hasToString(Problem.CONTENT_TYPE);

    Problem problem = objectMapper.readValue(response.getBody(), Problem.class);

    assertThat(problem).isEqualTo(Problem.builder().status(ProblemStatus.BAD_REQUEST).build());
  }
}
