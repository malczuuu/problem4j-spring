package io.github.malczuuu.problem4j.spring.webflux.integration;

import static org.hamcrest.Matchers.notNullValue;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.webflux.app.WebFluxTestApp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(
    classes = {WebFluxTestApp.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class MalformedMultipartWebFluxTest {

  @Autowired private WebTestClient webTestClient;

  @Test
  void givenRequestWithMalformedRequestPart_shouldReturnProblem() {
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
        .value(notNullValue())
        .isEqualTo(Problem.builder().status(ProblemStatus.BAD_REQUEST).build());
  }
}
