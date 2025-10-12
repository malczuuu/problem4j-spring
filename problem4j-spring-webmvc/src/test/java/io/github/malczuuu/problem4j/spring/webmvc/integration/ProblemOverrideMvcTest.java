package io.github.malczuuu.problem4j.spring.webmvc.integration;

import static io.github.malczuuu.problem4j.spring.webmvc.integration.ProblemOverrideMvcTest.InstanceOverrideController;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemException;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import java.net.URI;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest(
    classes = {_TestApp.class},
    properties = {
      "problem4j.type-override=https://example.org/type/{problem.type}",
      "problem4j.instance-override=https://example.org/trace/{context.traceId}",
      "problem4j.tracing-header-name=X-Trace-Id"
    })
@Import({InstanceOverrideController.class})
@AutoConfigureMockMvc
class ProblemOverrideMvcTest {

  @RestController
  static class InstanceOverrideController {
    @PostMapping(path = "/instance-override")
    String instanceOverride() {
      throw new ProblemException(Problem.builder().status(ProblemStatus.BAD_REQUEST).build());
    }

    @PostMapping(path = "/type-not-blank")
    String typeNotBlank() {
      throw new ProblemException(
          Problem.builder().type("not-blank").status(ProblemStatus.BAD_REQUEST).build());
    }
  }

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void givenNonEmptyType_shouldRewriteType() throws Exception {
    mockMvc
        .perform(post("/type-not-blank").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertThat(result.getResolvedException()).isInstanceOf(ProblemException.class))
        .andExpect(content().contentType(Problem.CONTENT_TYPE))
        .andExpect(
            result -> {
              Problem problem =
                  objectMapper.readValue(result.getResponse().getContentAsString(), Problem.class);

              assertThat(problem.getType())
                  .isEqualTo(URI.create("https://example.org/type/not-blank"));
            });
  }

  @Test
  void givenEmptyType_shouldNotRewriteType() throws Exception {
    mockMvc
        .perform(post("/instance-override").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isBadRequest())
        .andExpect(
            result -> {
              Problem problem =
                  objectMapper.readValue(result.getResponse().getContentAsString(), Problem.class);

              assertThat(problem.getType()).isEqualTo(Problem.BLANK_TYPE);
            });
  }

  @Test
  void givenNonEmptyTraceId_shouldRewriteInstanceField() throws Exception {
    String traceId = "12345-trace";

    mockMvc
        .perform(
            post("/instance-override")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Trace-ID", traceId)
                .content("{}"))
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertThat(result.getResolvedException()).isInstanceOf(ProblemException.class))
        .andExpect(content().contentType(Problem.CONTENT_TYPE))
        .andExpect(header().string("X-Trace-ID", traceId))
        .andExpect(
            result -> {
              Problem problem =
                  objectMapper.readValue(result.getResponse().getContentAsString(), Problem.class);

              assertThat(problem.getInstance())
                  .isEqualTo(URI.create("https://example.org/trace/" + traceId));
            });
  }
}
