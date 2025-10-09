package io.github.malczuuu.problem4j.spring.webflux.integration;

import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.ERRORS_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.VALIDATION_FAILED_DETAIL;
import static io.github.malczuuu.problem4j.spring.webflux.integration.ValidateRequestBodyWebFluxTest.ValidateRequestBodyController;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
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
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest(classes = {_TestApp.class})
@Import({ValidateRequestBodyController.class})
@AutoConfigureWebTestClient
class ValidateRequestBodyWebFluxTest {

  @RestController
  static class ValidateRequestBodyController {

    @PostMapping("/validate-request-body")
    String validateRequestBody(@Valid @RequestBody TestRequest request) {
      return "OK";
    }

    @PostMapping("/validate-global-object")
    String validateGlobalObject(@Valid @RequestBody AlwaysInvalidRequest body) {
      return "OK";
    }
  }

  @Autowired private WebTestClient webTestClient;

  @Test
  void givenInvalidRequestBody_shouldReturnProblem() {
    TestRequest invalidRequest = new TestRequest("", null);

    webTestClient
        .post()
        .uri("/validate-request-body")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(invalidRequest)
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.BAD_REQUEST)
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
                                List.of(Map.of("field", "name", "error", "must not be blank")))
                            .build()));
  }

  @Test
  void givenGlobalValidationViolation_shouldReturnProblemWithoutFieldName() {
    webTestClient
        .post()
        .uri("/validate-global-object")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue("{\"field\":\"value\"}")
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.BAD_REQUEST)
        .expectHeader()
        .contentType(Problem.CONTENT_TYPE)
        .expectBody(Problem.class)
        .value(
            problem -> {
              Map<String, String> error = new HashMap<>();
              error.put("field", null);
              error.put("error", "always invalid");
              Assertions.assertThat(problem)
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
  void givenMalformedRequestBody_shouldReturnProblem(String json) {
    WebTestClient.RequestBodySpec spec =
        webTestClient.post().uri("/validate-request-body").contentType(MediaType.APPLICATION_JSON);

    if (json != null) {
      spec.bodyValue(json);
    }

    spec.exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.BAD_REQUEST)
        .expectHeader()
        .contentType(Problem.CONTENT_TYPE)
        .expectBody(Problem.class)
        .isEqualTo(Problem.builder().status(ProblemStatus.BAD_REQUEST).build());
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
