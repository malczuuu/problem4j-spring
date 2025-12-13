package io.github.malczuuu.problem4j.spring.webmvc.app.rest;

import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/unsupported-media-type")
public class UnsupportedMediaTypeController {

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  String unsupportedMediaType(@RequestBody Map<String, Object> body) {
    return "OK";
  }
}
