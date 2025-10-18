package io.github.malczuuu.problem4j.spring.webmvc.integration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.webmvc.app.MvcTestApp;
import io.github.malczuuu.problem4j.spring.webmvc.integration.UnsupportedMediaTypeMvcTest.UnsupportedMediaTypeController;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.test.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.json.JsonMapper;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {MvcTestApp.class})
@Import({UnsupportedMediaTypeController.class})
class UnsupportedMediaTypeMvcTest {

  @RestController
  static class UnsupportedMediaTypeController {
    @PostMapping(path = "/unsupported-media-type", consumes = MediaType.APPLICATION_JSON_VALUE)
    String unsupportedMediaType(@RequestBody Map<String, Object> body) {
      return "OK";
    }
  }

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private JsonMapper jsonMapper;

  @Test
  void givenRequestWithUnsupportedContentType_shouldReturnProblem() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.TEXT_PLAIN);

    HttpEntity<String> request = new HttpEntity<>("some text", headers);

    ResponseEntity<String> response =
        restTemplate.postForEntity("/unsupported-media-type", request, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    assertThat(response.getHeaders().getContentType()).hasToString(Problem.CONTENT_TYPE);

    Problem problem = jsonMapper.readValue(response.getBody(), Problem.class);

    assertThat(problem)
        .isEqualTo(Problem.builder().status(ProblemStatus.UNSUPPORTED_MEDIA_TYPE).build());
  }
}
