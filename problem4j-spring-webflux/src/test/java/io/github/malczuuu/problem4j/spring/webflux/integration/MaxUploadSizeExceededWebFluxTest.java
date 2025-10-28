package io.github.malczuuu.problem4j.spring.webflux.integration;

import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.MAX_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.MAX_UPLOAD_SIZE_EXCEEDED_DETAIL;
import static io.github.malczuuu.problem4j.spring.webflux.integration.MaxUploadSizeExceededWebFluxTest.MaxUploadController;
import static org.hamcrest.Matchers.notNullValue;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.webflux.app.WebFluxTestApp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.AutoConfigureWebTestClient;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@SpringBootTest(
    classes = {WebFluxTestApp.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({MaxUploadController.class})
@AutoConfigureWebTestClient
class MaxUploadSizeExceededWebFluxTest {

  @RestController
  static class MaxUploadController {
    @PostMapping("/max-upload-size")
    String maxUploadSize() {
      throw new MaxUploadSizeExceededException(1L);
    }
  }

  @Autowired private WebTestClient webTestClient;

  @Test
  void givenMaxUploadSizeExceeded_shouldReturnProblem() {
    webTestClient
        .post()
        .uri("/max-upload-size")
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.CONTENT_TOO_LARGE)
        .expectHeader()
        .contentType(Problem.CONTENT_TYPE)
        .expectBody(Problem.class)
        .value(notNullValue())
        .isEqualTo(
            Problem.builder()
                .status(ProblemStatus.CONTENT_TOO_LARGE)
                .detail(MAX_UPLOAD_SIZE_EXCEEDED_DETAIL)
                .extension(MAX_EXTENSION, 1)
                .build());
  }
}
