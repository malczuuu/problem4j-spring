package io.github.malczuuu.problem4j.spring.webmvc.app.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/max-upload-size-exceeded")
public class MaxUploadSizeExceededController {

  @PostMapping
  public String maxUploadSize() {
    return "OK";
  }
}
