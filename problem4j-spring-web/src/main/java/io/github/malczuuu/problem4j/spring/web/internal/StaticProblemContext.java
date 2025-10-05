package io.github.malczuuu.problem4j.spring.web.internal;

import io.github.malczuuu.problem4j.spring.web.ProblemContext;
import org.jetbrains.annotations.ApiStatus;

/**
 * <b>For internal use only.</b>
 *
 * <p>This class is intended for internal use within the {@code problem4j-spring-*} libraries and
 * should not be used directly by external applications. The API may change or be removed without
 * notice.
 *
 * <p><b>Use at your own risk</b>
 *
 * @implNote This is an internal API and may change at any time.
 * @see ApiStatus.Internal
 */
@ApiStatus.Internal
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
