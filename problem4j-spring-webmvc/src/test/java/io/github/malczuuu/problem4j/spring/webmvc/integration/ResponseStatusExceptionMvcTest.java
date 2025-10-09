package io.github.malczuuu.problem4j.spring.webmvc.integration;

import static io.github.malczuuu.problem4j.spring.webmvc.integration.ResponseStatusExceptionMvcTest.ResponseStatusController;
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
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest(classes = {_TestApp.class})
@Import({ResponseStatusController.class})
@AutoConfigureMockMvc
class ResponseStatusExceptionMvcTest {

  @RestController
  static class ResponseStatusController {
    @GetMapping("/response-status-exception")
    String endpoint(@RequestParam(value = "reason", required = false) String reason) {
      if (reason == null) {
        throw new ResponseStatusException(HttpStatus.GONE);
      }
      throw new ResponseStatusException(HttpStatus.GONE, reason);
    }
  }

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void givenResponseStatusException_shouldReturnProblem() throws Exception {
    mockMvc
        .perform(get("/response-status-exception"))
        .andExpect(status().isGone())
        .andExpect(
            result ->
                assertThat(result.getResolvedException())
                    .isInstanceOf(ResponseStatusException.class))
        .andExpect(content().contentType(Problem.CONTENT_TYPE))
        .andExpect(
            result -> {
              Problem problem =
                  objectMapper.readValue(result.getResponse().getContentAsString(), Problem.class);
              assertThat(problem).isEqualTo(Problem.builder().status(ProblemStatus.GONE).build());
            });
  }

  @Test
  void givenResponseStatusExceptionWithReason_returnProblemWithStatusOnly() throws Exception {
    mockMvc
        .perform(get("/response-status-exception").param("reason", "resource gone"))
        .andExpect(status().isGone())
        .andExpect(
            result ->
                assertThat(result.getResolvedException())
                    .isInstanceOf(ResponseStatusException.class))
        .andExpect(content().contentType(Problem.CONTENT_TYPE))
        .andExpect(
            result -> {
              Problem problem =
                  objectMapper.readValue(result.getResponse().getContentAsString(), Problem.class);
              assertThat(problem).isEqualTo(Problem.builder().status(ProblemStatus.GONE).build());
            });
  }
}
