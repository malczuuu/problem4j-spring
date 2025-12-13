package io.github.malczuuu.problem4j.spring.webflux.app.problem;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemException;

public class ExtendedException extends ProblemException {

  public ExtendedException(String value1, Long value2, boolean value3) {
    super(
        Problem.builder()
            .type("https://example.org/extended/" + value1)
            .title("Extended Exception")
            .status(418)
            .detail("value2:" + value2)
            .instance("https://example.org/extended/instance/" + value3)
            .build());
  }
}
