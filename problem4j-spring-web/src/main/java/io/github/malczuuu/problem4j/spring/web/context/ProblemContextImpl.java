package io.github.malczuuu.problem4j.spring.web.context;

import java.util.Objects;

class ProblemContextImpl implements ProblemContext {

  static final ProblemContext EMPTY = ProblemContext.builder().build();

  private final String traceId;

  ProblemContextImpl(String traceId) {
    this.traceId = traceId;
  }

  @Override
  public String getTraceId() {
    return traceId;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof ProblemContext that)) {
      return false;
    }
    return Objects.equals(getTraceId(), that.getTraceId());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getTraceId());
  }

  @Override
  public String toString() {
    return "ProblemContext{traceId='" + getTraceId() + "'}";
  }
}
