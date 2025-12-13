package io.github.malczuuu.problem4j.spring.webflux.app.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestController
@RequestMapping(path = "/max-upload-size-exceeded")
public class MaxUploadSizeExceededController {

  @PostMapping
  public String maxUploadSize() {
    throw new MaxUploadSizeExceededException(1L);
  }
}
