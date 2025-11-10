package io.github.malczuuu.problem4j.spring.webmvc.integration;

import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.MAX_UPLOAD_SIZE_EXCEEDED_DETAIL;
import static io.github.malczuuu.problem4j.spring.webmvc.integration.MaxUploadSizeExceededMvcTest.MaxUploadController;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.webmvc.app.MvcTestApp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest(
    classes = {MvcTestApp.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
      "spring.servlet.multipart.max-file-size=1KB",
      "spring.servlet.multipart.max-request-size=1KB"
    })
@Import({MaxUploadController.class})
class MaxUploadSizeExceededMvcTest {

  @RestController
  static class MaxUploadController {
    @PostMapping("/max-upload-size")
    String maxUploadSize(@RequestParam("file") MultipartFile file) {
      return "OK";
    }
  }

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void givenMaxUploadSizeExceeded_shouldReturnProblem() throws Exception {
    MultiValueMap<String, Object> body = prepareMultipartBody();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

    ResponseEntity<String> response =
        restTemplate.postForEntity("/max-upload-size", requestEntity, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.PAYLOAD_TOO_LARGE);
    assertThat(response.getHeaders().getContentType()).hasToString(Problem.CONTENT_TYPE);

    Problem problem = objectMapper.readValue(response.getBody(), Problem.class);
    assertThat(problem)
        .isEqualTo(
            Problem.builder()
                .status(ProblemStatus.CONTENT_TOO_LARGE)
                .detail(MAX_UPLOAD_SIZE_EXCEEDED_DETAIL)
                .build());
  }

  private MultiValueMap<String, Object> prepareMultipartBody() {
    byte[] largeFile = new byte[1024 * 2];
    ByteArrayResource resource =
        new ByteArrayResource(largeFile) {
          @Override
          public String getFilename() {
            return "large-file.txt";
          }
        };

    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("file", resource);
    return body;
  }
}
