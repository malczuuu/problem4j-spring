package io.github.malczuuu.problem4j.spring.webmvc.integration;

import static io.github.malczuuu.problem4j.spring.webmvc.integration.ResponseStatusAnnotatedExceptionMvcTest.AnnotatedStatusController;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest(classes = {_TestApp.class})
@Import({AnnotatedStatusController.class})
@AutoConfigureMockMvc
class ResponseStatusAnnotatedExceptionMvcTest {

  @ResponseStatus(HttpStatus.FORBIDDEN)
  static class ForbiddenAnnotatedException extends RuntimeException {}

  @RestController
  static class AnnotatedStatusController {
    @GetMapping("/response-status-annotated")
    String endpoint() {
      throw new ForbiddenAnnotatedException();
    }
  }

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  // FIXME: support for @ResponseStatus is not implemented yet
  @Test
  void givenAnnotatedException_shouldReturnProblemWithStatus() throws Exception {
    mockMvc
        .perform(get("/response-status-annotated"))
        .andExpect(status().isInternalServerError())
        .andExpect(
            result ->
                assertThat(result.getResolvedException())
                    .isInstanceOf(ForbiddenAnnotatedException.class))
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
