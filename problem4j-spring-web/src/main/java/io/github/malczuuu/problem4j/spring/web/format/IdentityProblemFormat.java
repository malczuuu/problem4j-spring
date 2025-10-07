package io.github.malczuuu.problem4j.spring.web.format;

/** Convenience implementation for {@link ProblemFormat} which doesn't transform input data. */
public class IdentityProblemFormat implements ProblemFormat {

  @Override
  public String formatDetail(String detail) {
    return detail;
  }
}
