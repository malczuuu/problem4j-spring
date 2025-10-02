package io.github.malczuuu.problem4j.spring.webflux.integration;

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
@Import({
  MissingParameterTest.PathVariableController.class,
  MissingParameterTest.RequestParamController.class,
  MissingParameterTest.RequestPartController.class,
  MissingParameterTest.RequestHeaderController.class,
  MissingParameterTest.CookieValueController.class,
  MissingParameterTest.RequestAttributeController.class,
  MissingParameterTest.SessionAttributeController.class
})
@AutoConfigureWebTestClient
class MissingParameterTest {

  @Autowired private WebTestClient webTestClient;

  @RestController
  static class PathVariableController {
    @GetMapping(
        path = {"/missing-parameter/path-variable", "/missing-parameter/path-variable/{var}"})
    String endpoint(@PathVariable("var") String var) {
      return "OK";
    }
  }

  @Test
  void givenRequestWithoutPathVariable_shouldReturnProblemWithExtensions() {
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
                          .detail("Missing path variable")
                          .extension("name", "var")
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

  @RestController
  static class RequestParamController {
    @GetMapping(path = "/missing-parameter/request-param")
    String endpoint(@RequestParam("param") String param) {
      return "OK";
    }
  }

  @Test
  void givenRequestWithoutRequestParam_shouldReturnProblemWithExtensions() {
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
                          .detail("Missing request param")
                          .extension("param", "param")
                          .extension("kind", "string")
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

  @RestController
  static class RequestPartController {
    @PostMapping(path = "/missing-parameter/request-part")
    String endpoint(@RequestPart("file") FilePart file) {
      return "OK";
    }
  }

  @Test
  void givenRequestWithoutRequestPart_shouldReturnProblemWithExtensions() {
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
                          .detail("Missing request part")
                          .extension("param", "file")
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

  @RestController
  static class RequestHeaderController {
    @GetMapping(path = "/missing-parameter/request-header")
    String endpoint(@RequestHeader("X-Custom-Header") String xCustomHeader) {
      return "OK";
    }
  }

  @Test
  void givenRequestWithoutRequestHeader_shouldReturnProblemWithExtensions() {
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
                          .detail("Missing header")
                          .extension("header", "X-Custom-Header")
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

  @RestController
  static class CookieValueController {
    @GetMapping(path = "/missing-parameter/cookie-value")
    String endpoint(@CookieValue("x_session") String xSession) {
      return "OK";
    }
  }

  @Test
  void givenRequestWithoutCookieValue_shouldReturnProblemWithExtensions() {
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
                          .detail("Missing cookie")
                          .extension("cookie", "x_session")
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

  @RestController
  static class RequestAttributeController {
    @GetMapping(path = "/missing-parameter/request-attribute")
    String endpoint(@RequestAttribute("attr") String attr) {
      return "OK";
    }
  }

  @Test
  void givenRequestWithoutRequestAttribute_shouldReturnProblemWithExtensions() {
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
                          .detail("Missing request attribute")
                          .extension("attribute", "attr")
                          .build());
            });
  }

  @RestController
  static class SessionAttributeController {
    @GetMapping(path = "/missing-parameter/session-attribute")
    String endpoint(@SessionAttribute("attr") String attr) {
      return "OK";
    }
  }

  @Test
  void givenRequestWithoutSessionAttribute_shouldReturnProblemWithExtensions() {
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
                          .detail("Missing session attribute")
                          .extension("attribute", "attr")
                          .build());
            });
  }
}
