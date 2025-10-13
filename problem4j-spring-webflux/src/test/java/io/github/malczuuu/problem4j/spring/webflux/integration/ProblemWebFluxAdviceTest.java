package io.github.malczuuu.problem4j.spring.webflux.integration;

import static io.github.malczuuu.problem4j.spring.webflux.integration.ProblemWebFluxAdviceTest.ProblemExceptionController;
import static io.github.malczuuu.problem4j.spring.webflux.integration.ProblemWebFluxAdviceTest.ResolvableExceptionResolver;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.core.ProblemException;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.annotation.ProblemMapping;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.resolver.ProblemResolver;
import io.github.malczuuu.problem4j.spring.webflux.app.WebFluxTestApp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest(classes = {WebFluxTestApp.class})
@Import({ProblemExceptionController.class, ResolvableExceptionResolver.class})
@AutoConfigureWebTestClient
class ProblemWebFluxAdviceTest {

  @RestController
  static class ProblemExceptionController {

    @GetMapping("/problem/exception")
    String exception(
        @RequestParam("value1") String value1,
        @RequestParam("value2") Long value2,
        @RequestParam("value3") boolean value3) {
      throw new ExtendedException(value1, value2, value3);
    }

    @GetMapping("/problem/annotation")
    String annotation(
        @RequestParam("value1") String value1,
        @RequestParam("value2") Long value2,
        @RequestParam("value3") boolean value3) {
      throw new AnnotatedException(value1, value2, value3);
    }

    @GetMapping("/problem/annotation-empty")
    String annotationEmpty() {
      throw new AnnotationEmptyException("does not matter", -1L, false);
    }

    @GetMapping("/problem/resolvable")
    String resolvable() {
      throw new ResolvableException();
    }

    @GetMapping("/problem/unresolvable")
    String unresolvable() {
      throw new UnresolvableException();
    }
  }

  @Component
  static class ResolvableExceptionResolver implements ProblemResolver {
    @Override
    public Class<? extends Exception> getExceptionClass() {
      return ResolvableException.class;
    }

    @Override
    public ProblemBuilder resolveBuilder(
        ProblemContext context, Exception ex, HttpHeaders headers, HttpStatusCode status) {
      return Problem.builder()
          .type("http://exception.example.org/resolvable")
          .title(ex.getClass().getSimpleName())
          .status(422)
          .extension("package", ex.getClass().getPackageName());
    }
  }

  @Autowired private WebTestClient webTestClient;

  @ParameterizedTest
  @CsvSource({"string1, 1, true", "string2, 2, false"})
  void givenExtendedProblemException_shouldReturnProblem(
      String value1, Long value2, boolean value3) {
    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path("/problem/exception")
                    .queryParam("value1", value1)
                    .queryParam("value2", value2)
                    .queryParam("value3", value3)
                    .build())
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.I_AM_A_TEAPOT)
        .expectHeader()
        .contentType(Problem.CONTENT_TYPE)
        .expectBody(Problem.class)
        .isEqualTo(
            Problem.builder()
                .type("https://example.org/extended/" + value1)
                .title("Extended Exception")
                .status(418)
                .detail("value2:" + value2)
                .instance("https://example.org/extended/instance/" + value3)
                .build());
  }

  @ParameterizedTest
  @CsvSource({"string1, 1, true", "string2, 2, false"})
  void givenAnnotatedException_shouldReturnProblem(String value1, Long value2, boolean value3) {
    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path("/problem/annotation")
                    .queryParam("value1", value1)
                    .queryParam("value2", value2)
                    .queryParam("value3", value3)
                    .build())
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.I_AM_A_TEAPOT)
        .expectHeader()
        .contentType(Problem.CONTENT_TYPE)
        .expectBody(Problem.class)
        .isEqualTo(
            Problem.builder()
                .type("https://example.org/annotated/" + value1)
                .title("Annotated Exception")
                .status(418)
                .detail("value2:" + value2)
                .instance("https://example.org/annotated/instance/" + value3)
                .build());
  }

  @Test
  void givenAnnotationEmptyException_returnProblemWithUnknownStatus() {
    webTestClient
        .get()
        .uri(uriBuilder -> uriBuilder.path("/problem/annotation-empty").build())
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
        .expectHeader()
        .contentType(Problem.CONTENT_TYPE)
        .expectBody(Problem.class)
        .isEqualTo(Problem.builder().status(0).build());
  }

  @Test
  void givenResolvableException_shouldReturnProblem() {
    webTestClient
        .get()
        .uri(uriBuilder -> uriBuilder.path("/problem/resolvable").build())
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
        .expectHeader()
        .contentType(Problem.CONTENT_TYPE)
        .expectBody(Problem.class)
        .isEqualTo(
            Problem.builder()
                .type("http://exception.example.org/resolvable")
                .title(ResolvableException.class.getSimpleName())
                .status(422)
                .extension("package", ResolvableException.class.getPackageName())
                .build());
  }

  @Test
  void givenUnresolvableException_shouldReturnInternalServerErrorProblem() {
    webTestClient
        .get()
        .uri(uriBuilder -> uriBuilder.path("/problem/unresolvable").build())
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
        .expectHeader()
        .contentType(Problem.CONTENT_TYPE)
        .expectBody(Problem.class)
        .isEqualTo(Problem.builder().status(ProblemStatus.INTERNAL_SERVER_ERROR).build());
  }

  static class ExtendedException extends ProblemException {
    ExtendedException(String value1, Long value2, boolean value3) {
      super(
          Problem.builder()
              .type("https://example.org/extended/" + value1)
              .title("Extended Exception")
              .status(418)
              .detail("value2:" + value2)
              .instance("https://example.org/extended/instance/" + value3)
              .build());
    }
  }

  @ProblemMapping(
      type = "https://example.org/annotated/{value1}",
      title = "Annotated Exception",
      status = 418,
      detail = "value2:{value2}",
      instance = "https://example.org/annotated/instance/{value3}")
  static class AnnotatedException extends RuntimeException {

    private final String value1;
    private final Long value2;
    private final boolean value3;

    AnnotatedException(String value1, Long value2, boolean value3) {
      this.value1 = value1;
      this.value2 = value2;
      this.value3 = value3;
    }
  }

  @ProblemMapping
  static class AnnotationEmptyException extends RuntimeException {

    private final String value1;
    private final Long value2;
    private final boolean value3;

    AnnotationEmptyException(String value1, Long value2, boolean value3) {
      this.value1 = value1;
      this.value2 = value2;
      this.value3 = value3;
    }
  }

  static class ResolvableException extends RuntimeException {}

  static class UnresolvableException extends RuntimeException {}
}
