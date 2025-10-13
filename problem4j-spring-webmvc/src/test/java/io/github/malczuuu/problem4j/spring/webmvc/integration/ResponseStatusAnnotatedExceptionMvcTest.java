package io.github.malczuuu.problem4j.spring.webmvc.integration;

import static io.github.malczuuu.problem4j.spring.webmvc.integration.ResponseStatusAnnotatedExceptionMvcTest.AnnotatedStatusController;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.webmvc.app.MvcTestApp;
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

@SpringBootTest(classes = {MvcTestApp.class})
@Import({AnnotatedStatusController.class})
@AutoConfigureMockMvc
class ResponseStatusAnnotatedExceptionMvcTest {

  @ResponseStatus(HttpStatus.FORBIDDEN)
  static class ForbiddenAnnotatedException extends RuntimeException {}

  @ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "this is reason")
  static class ReasonAnnotatedException extends RuntimeException {}

  @RestController
  static class AnnotatedStatusController {

    @GetMapping("/response-status-annotated")
    String responseStatusAnnotated() {
      throw new ForbiddenAnnotatedException();
    }

    @GetMapping("/reason-annotated")
    String reasonAnnotated() {
      throw new ReasonAnnotatedException();
    }
  }

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void givenSpringNativeResponseStatusAnnotation_shouldReturnProblemWithStatus() throws Exception {
    mockMvc
        .perform(get("/response-status-annotated"))
        .andExpect(status().isForbidden())
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
                  .isEqualTo(Problem.builder().status(ProblemStatus.FORBIDDEN).build());
            });
  }

  @Test
  void givenSpringNativeResponseStatusAnnotationWithReason_shouldReturnProblem() throws Exception {
    mockMvc
        .perform(get("/reason-annotated"))
        .andExpect(status().isForbidden())
        .andExpect(
            result ->
                assertThat(result.getResolvedException())
                    .isInstanceOf(ReasonAnnotatedException.class))
        .andExpect(content().contentType(Problem.CONTENT_TYPE))
        .andExpect(
            result -> {
              Problem problem =
                  objectMapper.readValue(result.getResponse().getContentAsString(), Problem.class);
              assertThat(problem)
                  .isEqualTo(
                      Problem.builder()
                          .status(ProblemStatus.FORBIDDEN)
                          .detail("this is reason")
                          .build());
            });
  }
}
