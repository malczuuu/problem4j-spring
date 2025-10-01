package io.github.malczuuu.problem4j.spring.web.integration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest(classes = {_TestApp.class})
@Import({HttpMessageNotWritableIntegrationTest.TestController.class})
@AutoConfigureMockMvc
class HttpMessageNotWritableIntegrationTest {

  static class TestResponse {
    TestResponse self;
  }

  @RestController
  static class TestController {
    @GetMapping(path = "/endpoint", produces = MediaType.APPLICATION_JSON_VALUE)
    TestResponse endpoint() {
      TestResponse response = new TestResponse();
      response.self = response;
      return response;
    }
  }

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Test
  void givenException_shouldOverrideIt() throws Exception {
    mockMvc
        .perform(get("/endpoint").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(
            result ->
                assertThat(result.getResolvedException())
                    .isInstanceOf(HttpMessageNotWritableException.class))
        .andExpect(content().contentType(Problem.CONTENT_TYPE))
        .andExpect(
            result -> {
              Problem problem =
                  objectMapper.readValue(result.getResponse().getContentAsString(), Problem.class);

              assertThat(problem)
                  .isEqualTo(Problem.builder().status(ProblemStatus.INTERNAL_SERVER_ERROR).build());
            });
  }
}
