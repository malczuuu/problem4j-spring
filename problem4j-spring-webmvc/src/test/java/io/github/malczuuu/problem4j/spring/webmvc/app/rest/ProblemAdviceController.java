package io.github.malczuuu.problem4j.spring.webmvc.app.rest;

import io.github.malczuuu.problem4j.spring.webmvc.app.problem.AnnotatedException;
import io.github.malczuuu.problem4j.spring.webmvc.app.problem.AnnotationEmptyException;
import io.github.malczuuu.problem4j.spring.webmvc.app.problem.ExtendedException;
import io.github.malczuuu.problem4j.spring.webmvc.app.problem.ResolvableException;
import io.github.malczuuu.problem4j.spring.webmvc.app.problem.UnresolvableException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/problem-advice")
public class ProblemAdviceController {

  @GetMapping(path = "/exception")
  public String exception(
      @RequestParam("value1") String value1,
      @RequestParam("value2") Long value2,
      @RequestParam("value3") boolean value3) {
    throw new ExtendedException(value1, value2, value3);
  }

  @GetMapping(path = "/annotation")
  public String annotation(
      @RequestParam("value1") String value1,
      @RequestParam("value2") Long value2,
      @RequestParam("value3") boolean value3) {
    throw new AnnotatedException(value1, value2, value3);
  }

  @GetMapping(path = "/annotation-empty")
  public String annotationEmpty() {
    throw new AnnotationEmptyException("does not matter", -1L, false);
  }

  @GetMapping(path = "/resolvable")
  public String resolvable() {
    throw new ResolvableException();
  }

  @GetMapping(path = "/unresolvable")
  public String unresolvable() {
    throw new UnresolvableException();
  }
}
