package io.github.malczuuu.problem4j.spring.webflux.integration;

import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.KIND_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.PROPERTY_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.TYPE_MISMATCH_DETAIL;

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
@Import({TypeMismatchTest.TypeMismatchController.class})
@AutoConfigureWebTestClient
class TypeMismatchTest {

  @RestController
  static class TypeMismatchController {
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
                .detail(TYPE_MISMATCH_DETAIL)
                .extension(PROPERTY_EXTENSION, "id")
                .extension(KIND_EXTENSION, "integer")
                .build());
  }
}
