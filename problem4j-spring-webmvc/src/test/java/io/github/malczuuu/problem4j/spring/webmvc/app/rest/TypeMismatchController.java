package io.github.malczuuu.problem4j.spring.webmvc.app.rest;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/type-mismatch")
public class TypeMismatchController {

  @GetMapping(path = "/path-variable/{id}")
  public String pathVariable(@PathVariable("id") Integer id) {
    return "OK";
  }

  @GetMapping(path = "/request-param")
  public String requestParam(@RequestParam("id") Integer id) {
    return "OK";
  }

  @GetMapping(path = "/request-header")
  public String requestHeader(@RequestHeader("X-Id") Integer id) {
    return "OK";
  }

  @GetMapping(path = "/cookie-value")
  public String cookieValue(@CookieValue("id") Integer id) {
    return "OK";
  }
}
