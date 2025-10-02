package io.github.malczuuu.problem4j.spring.webflux.integration;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import org.junit.jupiter.api.Test;
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
@Import({TypeMismatchTest.TestController.class})
@AutoConfigureWebTestClient
class TypeMismatchTest {

  @RestController
  static class TestController {
    @GetMapping("/type-mismatch")
    String endpoint(@RequestParam("id") Integer id) {
      return "OK";
    }
  }

  @Autowired private WebTestClient webTestClient;

  @Test
  void givenException_shouldOverrideIt() {
    webTestClient
        .get()
        .uri(uriBuilder -> uriBuilder.path("/type-mismatch").queryParam("id", "abc").build())
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.BAD_REQUEST)
        .expectHeader()
        .contentType(Problem.CONTENT_TYPE)
        .expectBody(Problem.class)
        .isEqualTo(
            Problem.builder()
                .status(ProblemStatus.BAD_REQUEST)
                .detail("Type mismatch")
                .extension("property", "id")
                .extension("kind", "integer")
                .build());
  }
}
