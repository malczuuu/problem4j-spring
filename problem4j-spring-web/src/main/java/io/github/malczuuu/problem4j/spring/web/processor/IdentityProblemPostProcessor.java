package io.github.malczuuu.problem4j.spring.web.processor;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;

/**
 * Convenience implementation for {@link ProblemPostProcessor} which doesn't transform input data.
 */
public class IdentityProblemPostProcessor implements ProblemPostProcessor {

  /**
   * Returns the given {@link Problem} unchanged.
   *
   * @param context optional problem context (ignored)
   * @param problem the problem instance to pass through (may be {@code null})
   * @return the same instance provided in {@code problem}
   */
  @Override
  public Problem process(ProblemContext context, Problem problem) {
    return problem;
  }
}
