package io.github.malczuuu.problem4j.spring.web.format;

/** Convenience implementation for {@link ProblemFormat} which doesn't transform input data. */
public class IdentityProblemFormat implements ProblemFormat {

  /**
   * Returns the input detail unchanged (identity formatting).
   *
   * @param detail original detail text (may be {@code null})
   * @return the same {@code detail} value
   */
  @Override
  public String formatDetail(String detail) {
    return detail;
  }
}
