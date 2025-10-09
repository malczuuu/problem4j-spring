package io.github.malczuuu.problem4j.spring.webmvc.integration;

import static io.github.malczuuu.problem4j.spring.webmvc.integration.NotAcceptableMvcTest.NotAcceptableController;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest(classes = {_TestApp.class})
@Import({NotAcceptableController.class})
@AutoConfigureMockMvc
class NotAcceptableMvcTest {

  @RestController
  static class NotAcceptableController {
    @GetMapping(path = "/not-acceptable", produces = MediaType.TEXT_PLAIN_VALUE)
    String noteAcceptable() {
      return "OK";
    }
  }

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void givenUnsupportedAcceptHeader_shouldReturnProblem() throws Exception {
    mockMvc
        .perform(get("/not-acceptable").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotAcceptable())
        .andExpect(
            result ->
                assertThat(result.getResolvedException())
                    .isInstanceOf(HttpMediaTypeNotAcceptableException.class))
        .andExpect(content().contentType(Problem.CONTENT_TYPE))
        .andExpect(
            result -> {
              Problem problem =
                  objectMapper.readValue(result.getResponse().getContentAsString(), Problem.class);

              assertThat(problem)
                  .isEqualTo(Problem.builder().status(ProblemStatus.NOT_ACCEPTABLE).build());
            });
  }
}
