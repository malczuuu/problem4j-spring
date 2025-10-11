package io.github.malczuuu.problem4j.spring.webmvc.integration;

import static io.github.malczuuu.problem4j.spring.webmvc.integration.InstanceOverrideMvcTest.InstanceOverrideController;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.malczuuu.problem4j.core.Problem;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.net.URI;
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
      "problem4j.instance-override=https://example.org/trace/{context.traceId}",
      "problem4j.tracing-header-name=X-Trace-Id"
    })
@Import({InstanceOverrideController.class})
@AutoConfigureMockMvc
class InstanceOverrideMvcTest {

  record TestRequest(@NotBlank String name) {}

  @RestController
  static class InstanceOverrideController {
    @PostMapping(path = "/instance-override")
    String instanceOverride(@Valid @RequestBody TestRequest request) {
      return "OK";
    }
  }

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void givenInstanceOverrideEnabled_shouldIncludeInstanceFieldWithTraceId() throws Exception {
    String traceId = "12345-trace";

    TestRequest invalidRequest = new TestRequest("");

    mockMvc
        .perform(
            post("/instance-override")
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

              assertThat(problem.getInstance())
                  .isEqualTo(URI.create("https://example.org/trace/" + traceId));
            });
  }
}
