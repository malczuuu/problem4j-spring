package io.github.malczuuu.problem4j.spring.webmvc.app.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(path = "/malformed-multipart")
public class MalformedMultipartController {

  @PostMapping
  public String malformedMultipart(@RequestPart("file") MultipartFile file) {
    return "OK";
  }
}
