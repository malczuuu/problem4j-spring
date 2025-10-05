package io.github.malczuuu.problem4j.spring.webflux.integration;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemException;
import io.github.malczuuu.problem4j.spring.web.annotation.ProblemMapping;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest(classes = {_TestApp.class})
@Import({
  ProblemWebFluxAdviceTest.ProblemExceptionController.class,
  ProblemWebFluxAdviceTest.ProblemAnnotationController.class
})
@AutoConfigureWebTestClient
class ProblemWebFluxAdviceTest {

  static class ExtendedException extends ProblemException {
    ExtendedException(String value1, Long value2, boolean value3) {
      super(
          Problem.builder()
              .type("https://example.com/extended/" + value1)
              .title("Extended Exception")
              .status(418)
              .detail("value2:" + value2)
              .instance("https://example.com/extended/instance/" + value3)
              .build());
    }
  }

  @ProblemMapping(
      type = "https://example.com/annotated/{value1}",
      title = "Annotated Exception",
      status = 418,
      detail = "value2:{value2}",
      instance = "https://example.com/annotated/instance/{value3}")
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

  @RestController
  static class ProblemExceptionController {
    @GetMapping("/problem/exception")
    String endpoint(
        @RequestParam("value1") String value1,
        @RequestParam("value2") Long value2,
        @RequestParam("value3") boolean value3) {
      throw new ExtendedException(value1, value2, value3);
    }
  }

  @RestController
  static class ProblemAnnotationController {
    @GetMapping("/problem/annotation")
    String endpoint(
        @RequestParam("value1") String value1,
        @RequestParam("value2") Long value2,
        @RequestParam("value3") boolean value3) {
      throw new AnnotatedException(value1, value2, value3);
    }
  }

  @Autowired private WebTestClient webTestClient;

  @ParameterizedTest
  @CsvSource({"string1, 1, true", "string2, 2, false"})
  void givenExtendedException_shouldOverrideIt(String value1, Long value2, boolean value3) {
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
                .type("https://example.com/extended/" + value1)
                .title("Extended Exception")
                .status(418)
                .detail("value2:" + value2)
                .instance("https://example.com/extended/instance/" + value3)
                .build());
  }

  @ParameterizedTest
  @CsvSource({"string1, 1, true", "string2, 2, false"})
  void givenAnnotatedException_shouldOverrideIt(String value1, Long value2, boolean value3) {
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
                .type("https://example.com/annotated/" + value1)
                .title("Annotated Exception")
                .status(418)
                .detail("value2:" + value2)
                .instance("https://example.com/annotated/instance/" + value3)
                .build());
  }
}
