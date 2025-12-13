package io.github.malczuuu.problem4j.spring.webmvc.app.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/method-not-allowed")
public class MethodNotAllowedController {

  @GetMapping
  public String methodNotAllowed() {
    return "OK";
  }
}
