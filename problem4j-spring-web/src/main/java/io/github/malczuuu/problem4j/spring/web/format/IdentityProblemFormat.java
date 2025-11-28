package io.github.malczuuu.problem4j.spring.web.format;

import org.springframework.lang.Nullable;

/** Convenience implementation for {@link ProblemFormat} which doesn't transform input data. */
public class IdentityProblemFormat implements ProblemFormat {

  /**
   * Returns the input detail unchanged (identity formatting).
   *
   * @param detail original detail text (may be {@code null})
   * @return the same {@code detail} value
   */
  @Nullable
  @Override
  public String formatDetail(@Nullable String detail) {
    return detail;
  }
}
