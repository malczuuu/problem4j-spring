package io.github.malczuuu.problem4j.spring.webflux.integration;

import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.MAX_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.MAX_UPLOAD_SIZE_EXCEEDED_DETAIL;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.webflux.app.WebFluxTestApp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(
    classes = {WebFluxTestApp.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class MaxUploadSizeExceededWebFluxTest {

  @Autowired private WebTestClient webTestClient;

  @Test
  void givenMaxUploadSizeExceeded_shouldReturnProblem() {
    webTestClient
        .post()
        .uri("/max-upload-size-exceeded")
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.CONTENT_TOO_LARGE)
        .expectHeader()
        .contentType(Problem.CONTENT_TYPE)
        .expectBody(Problem.class)
        .value(v -> assertThat(v).isNotNull())
        .isEqualTo(
            Problem.builder()
                .status(ProblemStatus.CONTENT_TOO_LARGE)
                .detail(MAX_UPLOAD_SIZE_EXCEEDED_DETAIL)
                .extension(MAX_EXTENSION, 1)
                .build());
  }
}
