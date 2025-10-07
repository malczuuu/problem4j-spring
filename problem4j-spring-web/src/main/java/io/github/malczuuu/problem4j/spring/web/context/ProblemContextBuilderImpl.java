package io.github.malczuuu.problem4j.spring.web.context;

class ProblemContextBuilderImpl implements ProblemContextBuilder {

  private String traceId;

  @Override
  public ProblemContextBuilderImpl traceId(Object traceId) {
    if (traceId != null) {
      this.traceId = traceId.toString();
    }
    return this;
  }

  @Override
  public ProblemContextBuilderImpl traceId(String traceId) {
    this.traceId = traceId;
    return this;
  }

  @Override
  public ProblemContext build() {
    return new ProblemContextImpl(traceId);
  }
}
