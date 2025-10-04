package io.github.malczuuu.problem4j.spring.web.util;

import io.github.malczuuu.problem4j.spring.web.ProblemContext;

public class StaticProblemContext implements ProblemContext {

  private final String traceId;

  public StaticProblemContext(Object traceId) {
    this(traceId != null ? traceId.toString() : null);
  }

  public StaticProblemContext(String traceId) {
    this.traceId = traceId;
  }

  @Override
  public String getTraceId() {
    return traceId;
  }
}
