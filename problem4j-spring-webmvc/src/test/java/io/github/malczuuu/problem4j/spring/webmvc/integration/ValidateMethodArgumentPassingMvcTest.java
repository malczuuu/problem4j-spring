package io.github.malczuuu.problem4j.spring.webmvc.integration;

import static io.github.malczuuu.problem4j.spring.webmvc.integration.ValidateMethodArgumentPassingMvcTest.ValidateParameterController;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest(classes = {_TestApp.class})
@Import({ValidateParameterController.class})
@AutoConfigureMockMvc
class ValidateMethodArgumentPassingMvcTest {

  @Validated
  @RestController
  static class ValidateParameterController {

    @GetMapping("/validate-parameter/path-variable/{id}")
    String validatePathVariable(@PathVariable("id") @Size(min = 5) String idVar) {
      return "OK";
    }

    @GetMapping("/validate-parameter/request-param")
    String validateRequestParam(@RequestParam("query") @Size(min = 5) String queryParam) {
      return "OK";
    }

    @GetMapping("/validate-parameter/request-header")
    String validateRequestHeader(
        @RequestHeader("X-Custom-Header") @Size(min = 5) String xCustomHeader) {
      return "OK";
    }

    @GetMapping("/validate-parameter/cookie-value")
    String validateCookieValue(@CookieValue("x_session") @Size(min = 5) String xSession) {
      return "OK";
    }

    @GetMapping("/validate-parameter/multi-constraint")
    String validateMultiConstraint(
        @RequestParam("input") @Size(min = 5) @Pattern(regexp = "i") String inputParam) {
      return "OK";
    }

    @GetMapping("/validate-parameter/two-arg")
    String validateTwoArguments(
        @RequestParam("first") @Size(min = 5) String firstParam,
        @RequestParam("second") String secondParam) {
      return "OK";
    }

    @GetMapping("/validate-parameter/three-arg")
    String validateThreeArguments(
        @RequestParam("first") String firstParam,
        @RequestParam("second") @Size(min = 5) String secondParam,
        @RequestParam("third") String thirdParam) {
      return "OK";
    }
  }

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  /**
   * @see ValidateParameterController#validatePathVariable(String)
   */
  @Test
  void givenValidPathVariable_shouldReturnOk() throws Exception {
    mockMvc
        .perform(get("/validate-parameter/path-variable/value"))
        .andExpect(status().isOk())
        .andExpect(content().string("OK"));
  }

  /**
   * @see ValidateParameterController#validateRequestParam(String)
   */
  @Test
  void givenValidRequestParam_shouldReturnOk() throws Exception {
    mockMvc
        .perform(get("/validate-parameter/request-param").param("query", "value"))
        .andExpect(status().isOk())
        .andExpect(content().string("OK"));
  }

  /**
   * @see ValidateParameterController#validateRequestHeader(String)
   */
  @Test
  void givenValidRequestHeader_shouldReturnOk() throws Exception {
    mockMvc
        .perform(get("/validate-parameter/request-header").header("X-Custom-Header", "value"))
        .andExpect(status().isOk())
        .andExpect(content().string("OK"));
  }

  /**
   * @see ValidateParameterController#validateCookieValue(String)
   */
  @Test
  void givenValidCookieValue_shouldReturnOk() throws Exception {
    mockMvc
        .perform(get("/validate-parameter/cookie-value").cookie(new Cookie("x_session", "value")))
        .andExpect(status().isOk())
        .andExpect(content().string("OK"));
  }

  /**
   * @see ValidateParameterController#validateMultiConstraint(String)
   */
  @Test
  void givenBothParamsValid_shouldReturnOk() throws Exception {
    mockMvc
        .perform(
            get("/validate-parameter/two-arg")
                .param("first", "validVal")
                .param("second", "anything"))
        .andExpect(status().isOk())
        .andExpect(content().string("OK"));
  }

  /**
   * @see ValidateParameterController#validateThreeArguments(String, String, String)
   */
  @Test
  void givenThreeParamsValid_shouldReturnOk() throws Exception {
    mockMvc
        .perform(
            get("/validate-parameter/three-arg")
                .param("first", "anything")
                .param("second", "validVal")
                .param("third", "anything"))
        .andExpect(status().isOk())
        .andExpect(content().string("OK"));
  }
}
