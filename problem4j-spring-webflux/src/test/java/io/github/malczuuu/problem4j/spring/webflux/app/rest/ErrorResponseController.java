package io.github.malczuuu.problem4j.spring.webflux.app.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/error-response")
public class ErrorResponseController {

  @GetMapping
  public String errorResponse() {
    throw new ErrorResponseException(
        HttpStatus.CONFLICT,
        ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "this is detail"),
        null);
  }
}
