package io.github.malczuuu.problem4j.spring.web.integration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest(classes = {_TestApp.class})
@Import({HttpMessageNotReadableIntegrationTest.TestController.class})
@AutoConfigureMockMvc
class HttpMessageNotReadableIntegrationTest {

  record TestRequest(@NotBlank String name) {}

  @RestController
  static class TestController {
    @PostMapping("/endpoint")
    String endpoint(@Valid @RequestBody TestRequest request) {
      return "OK";
    }
  }

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Test
  void givenException_shouldOverrideIt() throws Exception {
    String invalidJson = "{ \"name\": \"Alice\"";

    mockMvc
        .perform(post("/endpoint").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertThat(result.getResolvedException())
                    .isInstanceOf(HttpMessageNotReadableException.class))
        .andExpect(content().contentType(Problem.CONTENT_TYPE))
        .andExpect(
            result -> {
              Problem problem =
                  objectMapper.readValue(result.getResponse().getContentAsString(), Problem.class);

              assertThat(problem)
                  .isEqualTo(Problem.builder().status(ProblemStatus.BAD_REQUEST).build());
            });
  }
}
