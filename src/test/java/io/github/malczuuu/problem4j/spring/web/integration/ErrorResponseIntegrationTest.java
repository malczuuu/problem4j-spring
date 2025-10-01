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
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest(classes = {_TestApp.class})
@Import({ErrorResponseIntegrationTest.TestController.class})
@AutoConfigureMockMvc
class ErrorResponseIntegrationTest {

  static class CustomType {
    private String value;

    String getValue() {
      return value;
    }

    void setValue(String value) {
      this.value = value;
    }
  }

  @RestController
  static class TestController {
    @GetMapping("/endpoint")
    String endpoint() {
      throw new ErrorResponseException(
          HttpStatus.CONFLICT,
          ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "this is detail"),
          null);
    }
  }

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Test
  void givenException_shouldOverrideIt() throws Exception {
    mockMvc
        .perform(get("/endpoint"))
        .andExpect(status().isConflict())
        .andExpect(
            result ->
                assertThat(result.getResolvedException())
                    .isInstanceOf(ErrorResponseException.class))
        .andExpect(content().contentType(Problem.CONTENT_TYPE))
        .andExpect(
            result -> {
              Problem problem =
                  objectMapper.readValue(result.getResponse().getContentAsString(), Problem.class);

              assertThat(problem)
                  .isEqualTo(
                      Problem.builder()
                          .status(ProblemStatus.CONFLICT)
                          .detail("this is detail")
                          .build());
            });
  }
}
