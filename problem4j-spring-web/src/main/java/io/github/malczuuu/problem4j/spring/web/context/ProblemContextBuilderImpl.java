package io.github.malczuuu.problem4j.spring.web.context;

import org.springframework.lang.Nullable;

class ProblemContextBuilderImpl implements ProblemContextBuilder {

  private @Nullable String traceId;

  @Override
  public ProblemContextBuilderImpl traceId(@Nullable Object traceId) {
    if (traceId != null) {
      this.traceId = traceId.toString();
    }
    return this;
  }

  @Override
  public ProblemContextBuilderImpl traceId(@Nullable String traceId) {
    this.traceId = traceId;
    return this;
  }

  @Override
  public ProblemContext build() {
    return new ProblemContextImpl(traceId);
  }
}
