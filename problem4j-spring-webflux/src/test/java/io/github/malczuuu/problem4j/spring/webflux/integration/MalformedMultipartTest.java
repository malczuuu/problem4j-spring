package io.github.malczuuu.problem4j.spring.webflux.integration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest(classes = {_TestApp.class})
@Import({MalformedMultipartTest.RequestPartController.class})
@AutoConfigureWebTestClient
class MalformedMultipartTest {

  @Autowired private WebTestClient webTestClient;

  @RestController
  static class RequestPartController {
    @PostMapping(path = "/malformed-multipart")
    String endpoint(@RequestPart("file") MultipartFile file) {
      return "OK";
    }
  }

  @Test
  void givenRequestWithMalformedRequestPart_shouldReturnProblemWithExtensions() {
    webTestClient
        .post()
        .uri("/malformed-multipart")
        .contentType(MediaType.MULTIPART_FORM_DATA)
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
                  .isEqualTo(Problem.builder().status(ProblemStatus.BAD_REQUEST).build());
            });
  }
}
