package io.github.malczuuu.problem4j.spring.webflux.integration;

import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.ATTRIBUTE_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.COOKIE_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.HEADER_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.KIND_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.MISSING_COOKIE_DETAIL;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.MISSING_HEADER_DETAIL;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.MISSING_PATH_VARIABLE_DETAIL;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.MISSING_REQUEST_ATTRIBUTE_DETAIL;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.MISSING_REQUEST_PARAM_DETAIL;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.MISSING_REQUEST_PART_DETAIL;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.MISSING_SESSION_ATTRIBUTE_DETAIL;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.NAME_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.PARAM_EXTENSION;
import static io.github.malczuuu.problem4j.spring.webflux.integration.MissingParameterWebFluxTest.MissingParameterController;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.reactive.function.BodyInserters;

@SpringBootTest(classes = {_TestApp.class})
@Import({MissingParameterController.class})
@AutoConfigureWebTestClient
class MissingParameterWebFluxTest {

  @Autowired private WebTestClient webTestClient;

  @RestController
  static class MissingParameterController {

    @GetMapping(
        path = {"/missing-parameter/path-variable", "/missing-parameter/path-variable/{var}"})
    String pathVariable(@PathVariable("var") String var) {
      return "OK";
    }

    @GetMapping(path = "/missing-parameter/request-param")
    String requestParam(@RequestParam("param") String param) {
      return "OK";
    }

    @PostMapping(path = "/missing-parameter/request-part")
    String requestPart(@RequestPart("file") FilePart file) {
      return "OK";
    }

    @GetMapping(path = "/missing-parameter/request-header")
    String requestHeader(@RequestHeader("X-Custom-Header") String xCustomHeader) {
      return "OK";
    }

    @GetMapping(path = "/missing-parameter/cookie-value")
    String cookieValue(@CookieValue("x_session") String xSession) {
      return "OK";
    }

    @GetMapping(path = "/missing-parameter/request-attribute")
    String requestAttribute(@RequestAttribute("attr") String attr) {
      return "OK";
    }

    @GetMapping(path = "/missing-parameter/session-attribute")
    String sessionAttribute(@SessionAttribute("attr") String attr) {
      return "OK";
    }
  }

  @Test
  void givenRequestWithoutPathVariable_shouldReturnProblem() {
    webTestClient
        .get()
        .uri("/missing-parameter/path-variable")
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectHeader()
        .contentType(Problem.CONTENT_TYPE)
        .expectBody(Problem.class)
        .consumeWith(
            res -> {
              Problem problem = res.getResponseBody();
              assertThat(problem)
                  .isEqualTo(
                      Problem.builder()
                          .status(ProblemStatus.BAD_REQUEST)
                          .detail(MISSING_PATH_VARIABLE_DETAIL)
                          .extension(NAME_EXTENSION, "var")
                          .build());
            });
  }

  @Test
  void givenRequestWithPathVariable_shouldReturnOk() {
    webTestClient
        .get()
        .uri("/missing-parameter/path-variable/value")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class)
        .isEqualTo("OK");
  }

  @Test
  void givenRequestWithoutRequestParam_shouldReturnProblem() {
    webTestClient
        .get()
        .uri("/missing-parameter/request-param")
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectHeader()
        .contentType(Problem.CONTENT_TYPE)
        .expectBody(Problem.class)
        .consumeWith(
            res -> {
              Problem problem = res.getResponseBody();
              assertThat(problem)
                  .isEqualTo(
                      Problem.builder()
                          .status(ProblemStatus.BAD_REQUEST)
                          .detail(MISSING_REQUEST_PARAM_DETAIL)
                          .extension(PARAM_EXTENSION, "param")
                          .extension(KIND_EXTENSION, "string")
                          .build());
            });
  }

  @Test
  void givenRequestWithRequestParam_shouldReturnOk() {
    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path("/missing-parameter/request-param")
                    .queryParam("param", "value")
                    .build())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class)
        .isEqualTo("OK");
  }

  @Test
  void givenRequestWithoutRequestPart_shouldReturnProblem() {
    webTestClient
        .post()
        .uri("/missing-parameter/request-part")
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectHeader()
        .contentType(Problem.CONTENT_TYPE)
        .expectBody(Problem.class)
        .consumeWith(
            res -> {
              Problem problem = res.getResponseBody();
              assertThat(problem)
                  .isEqualTo(
                      Problem.builder()
                          .status(ProblemStatus.BAD_REQUEST)
                          .detail(MISSING_REQUEST_PART_DETAIL)
                          .extension(PARAM_EXTENSION, "file")
                          .build());
            });
  }

  @Test
  void givenRequestWithRequestPart_shouldReturnOk() {
    MultipartBodyBuilder builder = new MultipartBodyBuilder();
    builder.part(
        "file",
        new ByteArrayResource("test content".getBytes()) {
          @Override
          public String getFilename() {
            return "file.txt";
          }
        },
        MediaType.TEXT_PLAIN);

    webTestClient
        .post()
        .uri("/missing-parameter/request-part")
        .body(BodyInserters.fromMultipartData(builder.build()))
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class)
        .isEqualTo("OK");
  }

  @Test
  void givenRequestWithoutRequestHeader_shouldReturnProblem() {
    webTestClient
        .get()
        .uri("/missing-parameter/request-header")
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectHeader()
        .contentType(Problem.CONTENT_TYPE)
        .expectBody(Problem.class)
        .consumeWith(
            res -> {
              Problem problem = res.getResponseBody();
              assertThat(problem)
                  .isEqualTo(
                      Problem.builder()
                          .status(ProblemStatus.BAD_REQUEST)
                          .detail(MISSING_HEADER_DETAIL)
                          .extension(HEADER_EXTENSION, "X-Custom-Header")
                          .build());
            });
  }

  @Test
  void givenRequestWithRequestHeader_shouldReturnOk() {
    webTestClient
        .get()
        .uri("/missing-parameter/request-header")
        .header("X-Custom-Header", "value")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class)
        .isEqualTo("OK");
  }

  @Test
  void givenRequestWithoutCookieValue_shouldReturnProblem() {
    webTestClient
        .get()
        .uri("/missing-parameter/cookie-value")
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectHeader()
        .contentType(Problem.CONTENT_TYPE)
        .expectBody(Problem.class)
        .consumeWith(
            res -> {
              Problem problem = res.getResponseBody();
              assertThat(problem)
                  .isEqualTo(
                      Problem.builder()
                          .status(ProblemStatus.BAD_REQUEST)
                          .detail(MISSING_COOKIE_DETAIL)
                          .extension(COOKIE_EXTENSION, "x_session")
                          .build());
            });
  }

  @Test
  void givenRequestWithCookieValue_shouldReturnOk() {
    webTestClient
        .get()
        .uri("/missing-parameter/cookie-value")
        .cookie("x_session", "value")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class)
        .isEqualTo("OK");
  }

  @Test
  void givenRequestWithoutRequestAttribute_shouldReturnProblem() {
    webTestClient
        .get()
        .uri("/missing-parameter/request-attribute")
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectHeader()
        .contentType(Problem.CONTENT_TYPE)
        .expectBody(Problem.class)
        .consumeWith(
            res -> {
              Problem problem = res.getResponseBody();
              assertThat(problem)
                  .isEqualTo(
                      Problem.builder()
                          .status(ProblemStatus.BAD_REQUEST)
                          .detail(MISSING_REQUEST_ATTRIBUTE_DETAIL)
                          .extension(ATTRIBUTE_EXTENSION, "attr")
                          .build());
            });
  }

  @Test
  void givenRequestWithoutSessionAttribute_shouldReturnProblem() {
    webTestClient
        .get()
        .uri("/missing-parameter/session-attribute")
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectHeader()
        .contentType(Problem.CONTENT_TYPE)
        .expectBody(Problem.class)
        .consumeWith(
            res -> {
              Problem problem = res.getResponseBody();
              assertThat(problem)
                  .isEqualTo(
                      Problem.builder()
                          .status(ProblemStatus.BAD_REQUEST)
                          .detail(MISSING_SESSION_ATTRIBUTE_DETAIL)
                          .extension(ATTRIBUTE_EXTENSION, "attr")
                          .build());
            });
  }
}
