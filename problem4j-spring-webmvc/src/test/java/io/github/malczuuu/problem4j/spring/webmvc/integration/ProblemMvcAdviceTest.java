package io.github.malczuuu.problem4j.spring.webmvc.integration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.core.ProblemException;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.annotation.ProblemMapping;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.resolver.ProblemResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest(classes = {_TestApp.class})
@Import({
  ProblemMvcAdviceTest.ProblemExceptionController.class,
  ProblemMvcAdviceTest.ProblemAnnotationController.class,
  ProblemMvcAdviceTest.AnnotationEmptyController.class,
  ProblemMvcAdviceTest.ResolvableExceptionController.class,
  ProblemMvcAdviceTest.UnresolvableExceptionController.class,
  ProblemMvcAdviceTest.ResolvableExceptionResolver.class
})
@AutoConfigureMockMvc
class ProblemMvcAdviceTest {

  static class ExtendedException extends ProblemException {
    ExtendedException(String value1, Long value2, boolean value3) {
      super(
          Problem.builder()
              .type("https://example.org/extended/" + value1)
              .title("Extended Exception")
              .status(418)
              .detail("value2:" + value2)
              .instance("https://example.org/extended/instance/" + value3)
              .build());
    }
  }

  @ProblemMapping(
      type = "https://example.org/annotated/{value1}",
      title = "Annotated Exception",
      status = 418,
      detail = "value2:{value2}",
      instance = "https://example.org/annotated/instance/{value3}")
  static class AnnotatedException extends RuntimeException {

    private final String value1;
    private final Long value2;
    private final boolean value3;

    AnnotatedException(String value1, Long value2, boolean value3) {
      this.value1 = value1;
      this.value2 = value2;
      this.value3 = value3;
    }
  }

  @ProblemMapping
  static class AnnotationEmptyException extends RuntimeException {

    private final String value1;
    private final Long value2;
    private final boolean value3;

    AnnotationEmptyException(String value1, Long value2, boolean value3) {
      this.value1 = value1;
      this.value2 = value2;
      this.value3 = value3;
    }
  }

  static class ResolvableException extends RuntimeException {}

  static class UnresolvableException extends RuntimeException {}

  @Component
  static class ResolvableExceptionResolver implements ProblemResolver {
    @Override
    public Class<? extends Exception> getExceptionClass() {
      return ResolvableException.class;
    }

    @Override
    public ProblemBuilder resolve(
        ProblemContext context, Exception ex, HttpHeaders headers, HttpStatusCode status) {
      return Problem.builder()
          .type("http://exception.example.org/resolvable")
          .title(ex.getClass().getSimpleName())
          .status(422)
          .extension("package", ex.getClass().getPackageName());
    }
  }

  @RestController
  static class ProblemExceptionController {
    @GetMapping("/problem/exception")
    String endpoint(
        @RequestParam("value1") String value1,
        @RequestParam("value2") Long value2,
        @RequestParam("value3") boolean value3) {
      throw new ExtendedException(value1, value2, value3);
    }
  }

  @RestController
  static class ProblemAnnotationController {
    @GetMapping("/problem/annotation")
    String endpoint(
        @RequestParam("value1") String value1,
        @RequestParam("value2") Long value2,
        @RequestParam("value3") boolean value3) {
      throw new AnnotatedException(value1, value2, value3);
    }
  }

  @RestController
  static class AnnotationEmptyController {
    @GetMapping("/problem/annotation-empty")
    String endpoint() {
      throw new AnnotationEmptyException("does not matter", -1L, false);
    }
  }

  @RestController
  static class ResolvableExceptionController {
    @GetMapping("/problem/resolvable")
    String endpoint() {
      throw new ResolvableException();
    }
  }

  @RestController
  static class UnresolvableExceptionController {
    @GetMapping("/problem/unresolvable")
    String endpoint() {
      throw new UnresolvableException();
    }
  }

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @ParameterizedTest
  @CsvSource({
    "string1, 1, true",
    "string2, 2, false",
  })
  void givenExtendedException_shouldOverrideIt(String value1, Long value2, boolean value3)
      throws Exception {
    mockMvc
        .perform(
            get("/problem/exception")
                .param("value1", value1)
                .param("value2", String.valueOf(value2))
                .param("value3", String.valueOf(value3)))
        .andExpect(status().is(418))
        .andExpect(
            result ->
                assertThat(result.getResolvedException()).isInstanceOf(ExtendedException.class))
        .andExpect(content().contentType(Problem.CONTENT_TYPE))
        .andExpect(
            result -> {
              Problem problem =
                  objectMapper.readValue(result.getResponse().getContentAsString(), Problem.class);

              assertThat(problem)
                  .isEqualTo(
                      Problem.builder()
                          .type("https://example.org/extended/" + value1)
                          .title("Extended Exception")
                          .status(418)
                          .detail("value2:" + value2)
                          .instance("https://example.org/extended/instance/" + value3)
                          .build());
            });
  }

  @ParameterizedTest
  @CsvSource({
    "string1, 1, true",
    "string2, 2, false",
  })
  void givenAnnotatedException_shouldOverrideIt(String value1, Long value2, boolean value3)
      throws Exception {
    mockMvc
        .perform(
            get("/problem/annotation")
                .param("value1", value1)
                .param("value2", String.valueOf(value2))
                .param("value3", String.valueOf(value3)))
        .andExpect(status().is(418))
        .andExpect(
            result ->
                assertThat(result.getResolvedException()).isInstanceOf(AnnotatedException.class))
        .andExpect(content().contentType(Problem.CONTENT_TYPE))
        .andExpect(
            result -> {
              Problem problem =
                  objectMapper.readValue(result.getResponse().getContentAsString(), Problem.class);

              assertThat(problem)
                  .isEqualTo(
                      Problem.builder()
                          .type("https://example.org/annotated/" + value1)
                          .title("Annotated Exception")
                          .status(418)
                          .detail("value2:" + value2)
                          .instance("https://example.org/annotated/instance/" + value3)
                          .build());
            });
  }

  @Test
  void givenAnnotationEmptyException_shouldOverrideIt() throws Exception {
    mockMvc
        .perform(get("/problem/annotation-empty"))
        .andExpect(status().isInternalServerError())
        .andExpect(
            result ->
                assertThat(result.getResolvedException())
                    .isInstanceOf(AnnotationEmptyException.class))
        .andExpect(content().contentType(Problem.CONTENT_TYPE))
        .andExpect(
            result -> {
              Problem problem =
                  objectMapper.readValue(result.getResponse().getContentAsString(), Problem.class);
              assertThat(problem).isEqualTo(Problem.builder().status(0).build());
            });
  }

  @Test
  void givenResolvableException_shouldOverrideIt() throws Exception {
    mockMvc
        .perform(get("/problem/resolvable"))
        .andExpect(status().is(422))
        .andExpect(
            result ->
                assertThat(result.getResolvedException()).isInstanceOf(ResolvableException.class))
        .andExpect(content().contentType(Problem.CONTENT_TYPE))
        .andExpect(
            result -> {
              Problem problem =
                  objectMapper.readValue(result.getResponse().getContentAsString(), Problem.class);

              assertThat(problem)
                  .isEqualTo(
                      Problem.builder()
                          .type("http://exception.example.org/resolvable")
                          .title(ResolvableException.class.getSimpleName())
                          .status(422)
                          .extension("package", ResolvableException.class.getPackageName())
                          .build());
            });
  }

  @Test
  void givenUnresolvableException_shouldOverrideIt() throws Exception {
    mockMvc
        .perform(get("/problem/unresolvable"))
        .andExpect(status().is(500))
        .andExpect(
            result ->
                assertThat(result.getResolvedException()).isInstanceOf(UnresolvableException.class))
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
