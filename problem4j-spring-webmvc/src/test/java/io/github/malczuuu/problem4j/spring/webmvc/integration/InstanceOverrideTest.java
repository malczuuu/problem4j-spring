package io.github.malczuuu.problem4j.spring.webmvc.integration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest(
    classes = {_TestApp.class},
    properties = {
      "problem4j.instance-override=https://example.com/trace/{traceId}",
      "problem4j.tracing-header-name=X-Trace-Id"
    })
@Import({InstanceOverrideTest.InstanceOverrideController.class})
@AutoConfigureMockMvc
class InstanceOverrideTest {

  record TestRequest(@NotBlank String name) {}

  @RestController
  static class InstanceOverrideController {
    @PostMapping(path = "/validate-request-body")
    String endpoint(@Valid @RequestBody TestRequest request) {
      return "OK";
    }
  }

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void givenInvalidRequestBody_shouldReturnProblemWithViolations() throws Exception {
    String traceId = "12345-trace";

    TestRequest invalidRequest = new TestRequest("");

    mockMvc
        .perform(
            post("/validate-request-body")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Trace-ID", traceId)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertThat(result.getResolvedException())
                    .isInstanceOf(MethodArgumentNotValidException.class))
        .andExpect(content().contentType(Problem.CONTENT_TYPE))
        .andExpect(header().string("X-Trace-ID", traceId))
        .andExpect(
            result -> {
              Problem problem =
                  objectMapper.readValue(result.getResponse().getContentAsString(), Problem.class);

              assertThat(problem)
                  .isEqualTo(
                      Problem.builder()
                          .status(ProblemStatus.BAD_REQUEST)
                          .detail("Validation failed")
                          .instance("https://example.com/trace/" + traceId)
                          .extension(
                              "errors",
                              List.of(Map.of("field", "name", "error", "must not be blank")))
                          .build());
            });
  }
}
