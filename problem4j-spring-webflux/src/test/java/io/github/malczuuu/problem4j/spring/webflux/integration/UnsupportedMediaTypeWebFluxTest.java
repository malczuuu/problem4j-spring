package io.github.malczuuu.problem4j.spring.webflux.integration;

import static io.github.malczuuu.problem4j.spring.webflux.integration.UnsupportedMediaTypeWebFluxTest.UnsupportedMediaTypeController;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import java.util.Map;
import org.junit.jupiter.api.Test;
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
@Import({UnsupportedMediaTypeController.class})
@AutoConfigureWebTestClient
class UnsupportedMediaTypeWebFluxTest {

  @RestController
  static class UnsupportedMediaTypeController {
    @PostMapping(path = "/unsupported-media-type", consumes = MediaType.APPLICATION_JSON_VALUE)
    String unsupportedMediaType(@RequestBody Map<String, Object> body) {
      return "OK";
    }
  }

  @Autowired private WebTestClient webTestClient;

  @Test
  void givenRequestWithUnsupportedContentType_shouldReturnProblem() {
    webTestClient
        .post()
        .uri("/unsupported-media-type")
        .contentType(MediaType.TEXT_PLAIN)
        .bodyValue("some text")
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
        .expectHeader()
        .contentType(Problem.CONTENT_TYPE)
        .expectBody(Problem.class)
        .isEqualTo(Problem.builder().status(ProblemStatus.UNSUPPORTED_MEDIA_TYPE).build());
  }
}
