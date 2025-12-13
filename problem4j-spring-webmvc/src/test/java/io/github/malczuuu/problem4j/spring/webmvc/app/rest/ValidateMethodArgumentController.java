package io.github.malczuuu.problem4j.spring.webmvc.app.rest;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping(path = "/validate-parameter")
public class ValidateMethodArgumentController {

  @GetMapping(path = "/path-variable/{id}")
  public String validatePathVariable(@PathVariable("id") @Size(min = 5) String idVar) {
    return "OK";
  }

  @GetMapping(path = "/request-param")
  public String validateRequestParam(@RequestParam("query") @Size(min = 5) String queryParam) {
    return "OK";
  }

  @GetMapping(path = "/request-header")
  public String validateRequestHeader(
      @RequestHeader("X-Custom-Header") @Size(min = 5) String xCustomHeader) {
    return "OK";
  }

  @GetMapping(path = "/cookie-value")
  public String validateCookieValue(@CookieValue("x_session") @Size(min = 5) String xSession) {
    return "OK";
  }

  @GetMapping(path = "/multi-constraint")
  public String validateMultiConstraint(
      @RequestParam("input") @Size(min = 5) @Pattern(regexp = "i") String inputParam) {
    return "OK";
  }

  @GetMapping(path = "/two-arg")
  public String validateTwoArguments(
      @RequestParam("first") @Size(min = 5) String firstParam,
      @RequestParam("second") String secondParam) {
    return "OK";
  }

  @GetMapping(path = "/three-arg")
  public String validateThreeArguments(
      @RequestParam("first") String firstParam,
      @RequestParam("second") @Size(min = 5) String secondParam,
      @RequestParam("third") String thirdParam) {
    return "OK";
  }
}
