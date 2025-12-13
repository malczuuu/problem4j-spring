package io.github.malczuuu.problem4j.spring.webmvc.app.rest;

import io.github.malczuuu.problem4j.spring.webmvc.app.model.TestForm;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/method-argument-not-valid")
public class MethodArgumentNotValidController {

  @GetMapping
  String webExchangeBind(@Valid @ModelAttribute TestForm form) {
    return "OK";
  }
}
