package io.github.malczuuu.problem4j.spring.webflux.app.rest;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemException;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/problem-override")
public class ProblemOverrideController {

  @PostMapping(path = "/instance-override")
  public String instanceOverride() {
    throw new ProblemException(Problem.builder().status(ProblemStatus.BAD_REQUEST).build());
  }

  @PostMapping(path = "/type-not-blank")
  public String typeNotBlank() {
    throw new ProblemException(
        Problem.builder().type("not-blank").status(ProblemStatus.BAD_REQUEST).build());
  }
}
