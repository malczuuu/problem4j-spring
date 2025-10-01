package io.github.malczuuu.problem4j.spring.web.integration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest(classes = {_TestApp.class})
@Import({MaxUploadSizeExceededIntegrationTest.TestController.class})
@AutoConfigureMockMvc
class MaxUploadSizeExceededIntegrationTest {

  @RestController
  static class TestController {
    @PostMapping("/endpoint")
    String endpoint(@RequestPart("file") MultipartFile file) {
      if (file.getSize() > 1024) {
        throw new MaxUploadSizeExceededException(1024);
      }
      return "OK";
    }
  }

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Test
  void givenException_shouldOverrideIt() throws Exception {
    byte[] largeContent = new byte[2 * 1024];
    MockMultipartFile largeFile =
        new MockMultipartFile("file", "large.txt", "text/plain", largeContent);

    mockMvc
        .perform(multipart("/endpoint").file(largeFile))
        .andExpect(status().isPayloadTooLarge())
        .andExpect(
            result ->
                assertThat(result.getResolvedException())
                    .isInstanceOf(MaxUploadSizeExceededException.class))
        .andExpect(content().contentType(Problem.CONTENT_TYPE))
        .andExpect(
            result -> {
              Problem problem =
                  objectMapper.readValue(result.getResponse().getContentAsString(), Problem.class);

              assertThat(problem)
                  .isEqualTo(
                      Problem.builder()
                          .status(ProblemStatus.CONTENT_TOO_LARGE)
                          .detail("Max upload size exceeded")
                          .extension("max", 1024)
                          .build());
            });
  }
}
