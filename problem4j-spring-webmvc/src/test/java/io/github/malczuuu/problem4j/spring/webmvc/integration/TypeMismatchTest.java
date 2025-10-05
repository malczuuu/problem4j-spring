package io.github.malczuuu.problem4j.spring.webmvc.integration;

import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.KIND_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.PROPERTY_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.TYPE_MISMATCH_DETAIL;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest(classes = {_TestApp.class})
@Import({
  TypeMismatchTest.PathVariableController.class,
  TypeMismatchTest.RequestParamController.class,
  TypeMismatchTest.RequestHeaderController.class,
  TypeMismatchTest.CookieValueController.class
})
@AutoConfigureMockMvc
class TypeMismatchTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @RestController
  static class PathVariableController {
    @GetMapping(path = "/type-mismatch/path-variable/{id}")
    String endpoint(@PathVariable("id") Integer id) {
      return "OK";
    }
  }

  @Test
  void givenRequestWithInvalidPathVariable_shouldReturnProblemWithExtensions() throws Exception {
    mockMvc
        .perform(get("/type-mismatch/path-variable/abc"))
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertThat(result.getResolvedException()).isInstanceOf(TypeMismatchException.class))
        .andExpect(content().contentType(Problem.CONTENT_TYPE))
        .andExpect(
            result -> {
              Problem problem =
                  objectMapper.readValue(result.getResponse().getContentAsString(), Problem.class);
              assertThat(problem)
                  .isEqualTo(
                      Problem.builder()
                          .status(ProblemStatus.BAD_REQUEST)
                          .detail(TYPE_MISMATCH_DETAIL)
                          .extension(PROPERTY_EXTENSION, "id")
                          .extension(KIND_EXTENSION, "integer")
                          .build());
            });
  }

  @Test
  void givenRequestWithValidPathVariable_shouldReturnOk() throws Exception {
    mockMvc
        .perform(get("/type-mismatch/path-variable/123"))
        .andExpect(status().isOk())
        .andExpect(content().string("OK"));
  }

  @RestController
  static class RequestParamController {
    @GetMapping(path = "/type-mismatch/request-param")
    String endpoint(@RequestParam("id") Integer id) {
      return "OK";
    }
  }

  @Test
  void givenRequestWithoutInvalidParameterType_shouldReturnProblemWithExtensions()
      throws Exception {
    mockMvc
        .perform(get("/type-mismatch/request-param").param("id", "abc"))
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertThat(result.getResolvedException()).isInstanceOf(TypeMismatchException.class))
        .andExpect(content().contentType(Problem.CONTENT_TYPE))
        .andExpect(
            result -> {
              Problem problem =
                  objectMapper.readValue(result.getResponse().getContentAsString(), Problem.class);

              assertThat(problem)
                  .isEqualTo(
                      Problem.builder()
                          .status(ProblemStatus.BAD_REQUEST)
                          .detail(TYPE_MISMATCH_DETAIL)
                          .extension(PROPERTY_EXTENSION, "id")
                          .extension(KIND_EXTENSION, "integer")
                          .build());
            });
  }

  @Test
  void givenRequestWithValidParameter_shouldReturnOk() throws Exception {
    mockMvc
        .perform(get("/type-mismatch/request-param").param("id", "123"))
        .andExpect(status().isOk())
        .andExpect(content().string("OK"));
  }

  @RestController
  static class RequestHeaderController {
    @GetMapping(path = "/type-mismatch/request-header")
    String endpoint(@RequestHeader("X-Id") Integer id) {
      return "OK";
    }
  }

  @Test
  void givenRequestWithInvalidRequestHeader_shouldReturnProblemWithExtensions() throws Exception {
    mockMvc
        .perform(get("/type-mismatch/request-header").header("X-Id", "abc"))
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertThat(result.getResolvedException()).isInstanceOf(TypeMismatchException.class))
        .andExpect(content().contentType(Problem.CONTENT_TYPE))
        .andExpect(
            result -> {
              Problem problem =
                  objectMapper.readValue(result.getResponse().getContentAsString(), Problem.class);
              assertThat(problem)
                  .isEqualTo(
                      Problem.builder()
                          .status(ProblemStatus.BAD_REQUEST)
                          .detail(TYPE_MISMATCH_DETAIL)
                          .extension(PROPERTY_EXTENSION, "X-Id")
                          .extension(KIND_EXTENSION, "integer")
                          .build());
            });
  }

  @Test
  void givenRequestWithValidRequestHeader_shouldReturnOk() throws Exception {
    mockMvc
        .perform(get("/type-mismatch/request-header").header("X-Id", "123"))
        .andExpect(status().isOk())
        .andExpect(content().string("OK"));
  }

  @RestController
  static class CookieValueController {
    @GetMapping(path = "/type-mismatch/cookie-value")
    String endpoint(@CookieValue("id") Integer id) {
      return "OK";
    }
  }

  @Test
  void givenRequestWithInvalidCookieValue_shouldReturnProblemWithExtensions() throws Exception {
    mockMvc
        .perform(get("/type-mismatch/cookie-value").cookie(new Cookie("id", "abc")))
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertThat(result.getResolvedException()).isInstanceOf(TypeMismatchException.class))
        .andExpect(content().contentType(Problem.CONTENT_TYPE))
        .andExpect(
            result -> {
              Problem problem =
                  objectMapper.readValue(result.getResponse().getContentAsString(), Problem.class);
              assertThat(problem)
                  .isEqualTo(
                      Problem.builder()
                          .status(ProblemStatus.BAD_REQUEST)
                          .detail(TYPE_MISMATCH_DETAIL)
                          .extension(PROPERTY_EXTENSION, "id")
                          .extension(KIND_EXTENSION, "integer")
                          .build());
            });
  }

  @Test
  void givenRequestWithValidCookieValue_shouldReturnOk() throws Exception {
    mockMvc
        .perform(get("/type-mismatch/cookie-value").cookie(new Cookie("id", "123")))
        .andExpect(status().isOk())
        .andExpect(content().string("OK"));
  }
}
