package io.github.malczuuu.problem4j.spring.webflux.integration;

import static io.github.malczuuu.problem4j.spring.webflux.integration.InstanceOverrideWebFluxTest.InstanceOverrideController;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.github.malczuuu.problem4j.core.Problem;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.net.URI;
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

@SpringBootTest(
    classes = {_TestApp.class},
    properties = {
      "problem4j.instance-override=https://example.org/trace/{context.traceId}",
      "problem4j.tracing-header-name=X-Trace-Id"
    })
@Import({InstanceOverrideController.class})
@AutoConfigureWebTestClient
class InstanceOverrideWebFluxTest {

  record TestRequest(@NotBlank String name) {}

  @RestController
  static class InstanceOverrideController {
    @PostMapping("/instance-override")
    String instanceOverride(@Valid @RequestBody TestRequest request) {
      return "OK";
    }
  }

  @Autowired private WebTestClient webTestClient;

  @Test
  void givenInstanceOverrideEnabled_shouldIncludeInstanceFieldWithTraceId() {
    String traceId = "12345-trace";

    TestRequest invalidRequest = new TestRequest("");

    webTestClient
        .post()
        .uri("/instance-override")
        .header("X-Trace-Id", traceId)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(invalidRequest)
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.BAD_REQUEST)
        .expectHeader()
        .contentType(Problem.CONTENT_TYPE)
        .expectHeader()
        .value("X-Trace-Id", v -> assertThat(v).isEqualTo(traceId))
        .expectBody(Problem.class)
        .value(
            problem ->
                assertThat(problem.getInstance())
                    .isEqualTo(URI.create("https://example.org/trace/" + traceId)));
  }
}
