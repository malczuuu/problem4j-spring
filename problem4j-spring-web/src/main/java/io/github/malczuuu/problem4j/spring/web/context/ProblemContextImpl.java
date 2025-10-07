package io.github.malczuuu.problem4j.spring.web.context;

import java.util.Objects;

class ProblemContextImpl implements ProblemContext {

  static ProblemContext EMPTY = ProblemContext.builder().build();

  private final String traceId;

  ProblemContextImpl(String traceId) {
    this.traceId = traceId;
  }

  @Override
  public String getTraceId() {
    return traceId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProblemContextImpl that = (ProblemContextImpl) o;
    return Objects.equals(traceId, that.traceId);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(traceId);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "{traceId='" + traceId + "'}";
  }
}
