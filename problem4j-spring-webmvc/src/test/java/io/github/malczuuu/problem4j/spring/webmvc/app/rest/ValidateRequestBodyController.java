package io.github.malczuuu.problem4j.spring.webmvc.app.rest;

import io.github.malczuuu.problem4j.spring.webmvc.app.model.AlwaysInvalidRequest;
import io.github.malczuuu.problem4j.spring.webmvc.app.model.TestRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ValidateRequestBodyController {

  @PostMapping("/validate-request-body")
  public String validateRequestBody(@Valid @RequestBody TestRequest request) {
    return "OK";
  }

  @PostMapping("/validate-global-object")
  public String validateGlobalObject(@Valid @RequestBody AlwaysInvalidRequest body) {
    return "OK";
  }
}
