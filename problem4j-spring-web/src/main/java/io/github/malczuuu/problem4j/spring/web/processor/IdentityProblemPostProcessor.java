package io.github.malczuuu.problem4j.spring.web.processor;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;

/**
 * Convenience implementation for {@link ProblemPostProcessor} which doesn't transform input data.
 */
public class IdentityProblemPostProcessor implements ProblemPostProcessor {

  @Override
  public Problem process(ProblemContext context, Problem problem) {
    return problem;
  }
}
