package io.github.malczuuu.problem4j.spring.webflux.integration;

import static org.hamcrest.Matchers.notNullValue;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.webflux.app.WebFluxTestApp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(
    classes = {WebFluxTestApp.class},
    properties = {
      "spring.webflux.static-path-pattern=/**",
      "spring.web.resources.static-locations=classpath:/static/"
    },
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class NotFoundNoResourceFoundWebFluxTest {

  @Autowired private WebTestClient webTestClient;

  @Test
  void givenMissingStaticResource_shouldReturnProblem() {
    webTestClient
        .get()
        .uri("/not-existing-file.html")
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectHeader()
        .contentType(Problem.CONTENT_TYPE)
        .expectBody(Problem.class)
        .value(notNullValue())
        .isEqualTo(Problem.builder().status(ProblemStatus.NOT_FOUND).build());
  }
}
