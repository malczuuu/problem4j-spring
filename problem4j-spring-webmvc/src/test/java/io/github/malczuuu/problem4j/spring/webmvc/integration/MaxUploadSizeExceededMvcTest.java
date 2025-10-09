package io.github.malczuuu.problem4j.spring.webmvc.integration;

import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.MAX_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.MAX_UPLOAD_SIZE_EXCEEDED_DETAIL;
import static io.github.malczuuu.problem4j.spring.webmvc.integration.MaxUploadSizeExceededMvcTest.MaxUploadController;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@SpringBootTest(classes = {_TestApp.class})
@Import({MaxUploadController.class})
@AutoConfigureMockMvc
class MaxUploadSizeExceededMvcTest {

  @RestController
  static class MaxUploadController {
    @PostMapping("/max-upload-size")
    String maxUploadSize() {
      throw new MaxUploadSizeExceededException(1L);
    }
  }

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void givenMaxUploadSizeExceeded_shouldReturnProblem() throws Exception {
    mockMvc
        .perform(post("/max-upload-size"))
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
                          .detail(MAX_UPLOAD_SIZE_EXCEEDED_DETAIL)
                          .extension(MAX_EXTENSION, 1)
                          .build());
            });
  }
}
