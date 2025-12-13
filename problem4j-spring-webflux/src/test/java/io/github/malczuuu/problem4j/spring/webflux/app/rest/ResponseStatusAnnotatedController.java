package io.github.malczuuu.problem4j.spring.webflux.app.rest;

import io.github.malczuuu.problem4j.spring.webflux.app.problem.ForbiddenAnnotatedException;
import io.github.malczuuu.problem4j.spring.webflux.app.problem.ReasonAnnotatedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/response-status-annotated")
public class ResponseStatusAnnotatedController {

  @GetMapping("/forbidden-status-annotated")
  public String responseStatusAnnotated() {
    throw new ForbiddenAnnotatedException();
  }

  @GetMapping("/reason-annotated")
  public String reasonAnnotated() {
    throw new ReasonAnnotatedException();
  }
}
