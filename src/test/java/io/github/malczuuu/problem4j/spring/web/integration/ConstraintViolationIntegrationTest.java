package io.github.malczuuu.problem4j.spring.web.integration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest(classes = {_TestApp.class})
@Import({ConstraintViolationIntegrationTest.TestController.class})
@AutoConfigureMockMvc
class ConstraintViolationIntegrationTest {

  static class CustomType {
    private String value;

    String getValue() {
      return value;
    }

    void setValue(String value) {
      this.value = value;
    }
  }

  @Validated
  @RestController
  static class TestController {
    @GetMapping("/endpoint")
    String endpoint(@RequestParam("year") @Size(min = 4) String year) {
      return "OK";
    }
  }

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Test
  void givenException_shouldOverrideIt() throws Exception {
    mockMvc
        .perform(get("/endpoint").param("year", "123"))
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertThat(result.getResolvedException())
                    .isInstanceOf(ConstraintViolationException.class))
        .andExpect(content().contentType(Problem.CONTENT_TYPE))
        .andExpect(
            result -> {
              Problem problem =
                  objectMapper.readValue(result.getResponse().getContentAsString(), Problem.class);

              assertThat(problem)
                  .isEqualTo(
                      Problem.builder()
                          .status(ProblemStatus.BAD_REQUEST)
                          .detail("Validation failed")
                          .extension(
                              "errors",
                              List.of(
                                  Map.of(
                                      "field",
                                      "year",
                                      "error",
                                      "size must be between 4 and " + Integer.MAX_VALUE)))
                          .build());
            });
  }
}
