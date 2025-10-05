package io.github.malczuuu.problem4j.spring.webmvc.integration;

import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.ATTRIBUTE_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.COOKIE_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.HEADER_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.KIND_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.MISSING_COOKIE_DETAIL;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.MISSING_HEADER_DETAIL;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.MISSING_PATH_VARIABLE_DETAIL;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.MISSING_REQUEST_ATTRIBUTE_DETAIL;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.MISSING_REQUEST_PARAM_DETAIL;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.MISSING_REQUEST_PART_DETAIL;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.MISSING_SESSION_ATTRIBUTE_DETAIL;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.NAME_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.PARAM_EXTENSION;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@SpringBootTest(classes = {_TestApp.class})
@Import({
  MissingParameterTest.PathVariableController.class,
  MissingParameterTest.RequestParamController.class,
  MissingParameterTest.RequestPartController.class,
  MissingParameterTest.RequestHeaderController.class,
  MissingParameterTest.CookieValueController.class,
  MissingParameterTest.RequestAttributeController.class,
  MissingParameterTest.SessionAttributeController.class
})
@AutoConfigureMockMvc
class MissingParameterTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @RestController
  static class PathVariableController {
    @GetMapping(
        path = {"/missing-parameter/path-variable", "/missing-parameter/path-variable/{var}"})
    String endpoint(@PathVariable("var") String var) {
      return "OK";
    }
  }

  @Test
  void givenRequestWithoutPathVariable_shouldReturnProblemWithExtensions() throws Exception {
    mockMvc
        .perform(get("/missing-parameter/path-variable"))
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertThat(result.getResolvedException())
                    .isInstanceOf(MissingPathVariableException.class))
        .andExpect(content().contentType(Problem.CONTENT_TYPE))
        .andExpect(
            result -> {
              Problem problem =
                  objectMapper.readValue(result.getResponse().getContentAsString(), Problem.class);

              assertThat(problem)
                  .isEqualTo(
                      Problem.builder()
                          .status(ProblemStatus.BAD_REQUEST)
                          .detail(MISSING_PATH_VARIABLE_DETAIL)
                          .extension(NAME_EXTENSION, "var")
                          .build());
            });
  }

  @Test
  void givenRequestWithPathVariable_shouldReturnOk() throws Exception {
    mockMvc
        .perform(get("/missing-parameter/path-variable/value"))
        .andExpect(status().isOk())
        .andExpect(content().string("OK"));
  }

  @RestController
  static class RequestParamController {
    @GetMapping(path = "/missing-parameter/request-param")
    String endpoint(@RequestParam("param") String param) {
      return "OK";
    }
  }

  @Test
  void givenRequestWithoutRequestParam_shouldReturnProblemWithExtensions() throws Exception {
    mockMvc
        .perform(get("/missing-parameter/request-param"))
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertThat(result.getResolvedException())
                    .isInstanceOf(MissingServletRequestParameterException.class))
        .andExpect(content().contentType(Problem.CONTENT_TYPE))
        .andExpect(
            result -> {
              Problem problem =
                  objectMapper.readValue(result.getResponse().getContentAsString(), Problem.class);
              assertThat(problem)
                  .isEqualTo(
                      Problem.builder()
                          .status(ProblemStatus.BAD_REQUEST)
                          .detail(MISSING_REQUEST_PARAM_DETAIL)
                          .extension(PARAM_EXTENSION, "param")
                          .extension(KIND_EXTENSION, "string")
                          .build());
            });
  }

  @Test
  void givenRequestWithRequestParam_shouldReturnOk() throws Exception {
    mockMvc
        .perform(get("/missing-parameter/request-param").param("param", "value"))
        .andExpect(status().isOk())
        .andExpect(content().string("OK"));
  }

  @RestController
  static class RequestPartController {
    @PostMapping(path = "/missing-parameter/request-part")
    String endpoint(@RequestPart("file") MultipartFile file) {
      return "OK";
    }
  }

  @Test
  void givenRequestWithoutRequestPart_shouldReturnProblemWithExtensions() throws Exception {
    mockMvc
        .perform(multipart("/missing-parameter/request-part"))
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertThat(result.getResolvedException())
                    .isInstanceOf(MissingServletRequestPartException.class))
        .andExpect(content().contentType(Problem.CONTENT_TYPE))
        .andExpect(
            result -> {
              Problem problem =
                  objectMapper.readValue(result.getResponse().getContentAsString(), Problem.class);

              assertThat(problem)
                  .isEqualTo(
                      Problem.builder()
                          .status(ProblemStatus.BAD_REQUEST)
                          .detail(MISSING_REQUEST_PART_DETAIL)
                          .extension(PARAM_EXTENSION, "file")
                          .build());
            });
  }

  @Test
  void givenRequestWithRequestPart_shouldReturnOk() throws Exception {
    mockMvc
        .perform(
            multipart("/missing-parameter/request-part").file("file", "test content".getBytes()))
        .andExpect(status().isOk())
        .andExpect(content().string("OK"));
  }

  @RestController
  static class RequestHeaderController {
    @GetMapping(path = "/missing-parameter/request-header")
    String endpoint(@RequestHeader("X-Custom-Header") String xCustomHeader) {
      return "OK";
    }
  }

  @Test
  void givenRequestWithoutRequestHeader_shouldReturnProblemWithExtensions() throws Exception {
    mockMvc
        .perform(get("/missing-parameter/request-header"))
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertThat(result.getResolvedException())
                    .isInstanceOf(MissingRequestHeaderException.class))
        .andExpect(content().contentType(Problem.CONTENT_TYPE))
        .andExpect(
            result -> {
              Problem problem =
                  objectMapper.readValue(result.getResponse().getContentAsString(), Problem.class);

              assertThat(problem)
                  .isEqualTo(
                      Problem.builder()
                          .status(ProblemStatus.BAD_REQUEST)
                          .detail(MISSING_HEADER_DETAIL)
                          .extension(HEADER_EXTENSION, "X-Custom-Header")
                          .build());
            });
  }

  @Test
  void givenRequestWithRequestHeader_shouldReturnOk() throws Exception {
    mockMvc
        .perform(get("/missing-parameter/request-header").header("X-Custom-Header", "value"))
        .andExpect(status().isOk())
        .andExpect(content().string("OK"));
  }

  @RestController
  static class CookieValueController {
    @GetMapping(path = "/missing-parameter/cookie-value")
    String endpoint(@CookieValue("x_session") String xSession) {
      return "OK";
    }
  }

  @Test
  void givenRequestWithoutCookieValue_shouldReturnProblemWithExtensions() throws Exception {
    mockMvc
        .perform(get("/missing-parameter/cookie-value"))
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertThat(result.getResolvedException())
                    .isInstanceOf(MissingRequestCookieException.class))
        .andExpect(content().contentType(Problem.CONTENT_TYPE))
        .andExpect(
            result -> {
              Problem problem =
                  objectMapper.readValue(result.getResponse().getContentAsString(), Problem.class);

              assertThat(problem)
                  .isEqualTo(
                      Problem.builder()
                          .status(ProblemStatus.BAD_REQUEST)
                          .detail(MISSING_COOKIE_DETAIL)
                          .extension(COOKIE_EXTENSION, "x_session")
                          .build());
            });
  }

  @Test
  void givenRequestWithCookieValue_shouldReturnOk() throws Exception {
    mockMvc
        .perform(get("/missing-parameter/cookie-value").cookie(new Cookie("x_session", "value")))
        .andExpect(status().isOk())
        .andExpect(content().string("OK"));
  }

  @RestController
  static class RequestAttributeController {
    @GetMapping(path = "/missing-parameter/request-attribute")
    String endpoint(@RequestAttribute("attr") String attr) {
      return "OK";
    }
  }

  @Test
  void givenRequestWithoutRequestAttribute_shouldReturnProblemWithExtensions() throws Exception {
    mockMvc
        .perform(get("/missing-parameter/request-attribute"))
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertThat(result.getResolvedException())
                    .isInstanceOf(ServletRequestBindingException.class))
        .andExpect(content().contentType(Problem.CONTENT_TYPE))
        .andExpect(
            result -> {
              Problem problem =
                  objectMapper.readValue(result.getResponse().getContentAsString(), Problem.class);

              assertThat(problem)
                  .isEqualTo(
                      Problem.builder()
                          .status(ProblemStatus.BAD_REQUEST)
                          .detail(MISSING_REQUEST_ATTRIBUTE_DETAIL)
                          .extension(ATTRIBUTE_EXTENSION, "attr")
                          .build());
            });
  }

  @Test
  void givenRequestWithRequestAttribute_shouldReturnOk() throws Exception {
    mockMvc
        .perform(get("/missing-parameter/request-attribute").requestAttr("attr", "value"))
        .andExpect(status().isOk())
        .andExpect(content().string("OK"));
  }

  @RestController
  static class SessionAttributeController {
    @GetMapping(path = "/missing-parameter/session-attribute")
    String endpoint(@SessionAttribute("attr") String attr) {
      return "OK";
    }
  }

  @Test
  void givenRequestWithoutSessionAttribute_shouldReturnProblemWithExtensions() throws Exception {
    mockMvc
        .perform(get("/missing-parameter/session-attribute"))
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertThat(result.getResolvedException())
                    .isInstanceOf(ServletRequestBindingException.class))
        .andExpect(content().contentType(Problem.CONTENT_TYPE))
        .andExpect(
            result -> {
              Problem problem =
                  objectMapper.readValue(result.getResponse().getContentAsString(), Problem.class);

              assertThat(problem)
                  .isEqualTo(
                      Problem.builder()
                          .status(ProblemStatus.BAD_REQUEST)
                          .detail(MISSING_SESSION_ATTRIBUTE_DETAIL)
                          .extension(ATTRIBUTE_EXTENSION, "attr")
                          .build());
            });
  }

  @Test
  void givenRequestWithSessionAttribute_shouldReturnOk() throws Exception {
    mockMvc
        .perform(get("/missing-parameter/session-attribute").sessionAttr("attr", "value"))
        .andExpect(status().isOk())
        .andExpect(content().string("OK"));
  }
}
