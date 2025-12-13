package io.github.malczuuu.problem4j.spring.webflux.app.rest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/not-acceptable")
public class NotAcceptableController {

  @GetMapping(produces = MediaType.TEXT_PLAIN_VALUE)
  public String notAcceptable() {
    return "OK";
  }
}
