package io.github.malczuuu.problem4j.spring.webflux.app.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(path = "/response-status-exception")
public class ResponseStatusController {

  @GetMapping
  public String responseStatusException(
      @RequestParam(value = "reason", required = false) String reason) {
    if (reason == null) {
      throw new ResponseStatusException(HttpStatus.GONE);
    }
    throw new ResponseStatusException(HttpStatus.GONE, reason);
  }
}
