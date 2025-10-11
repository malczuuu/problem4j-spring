package io.github.malczuuu.problem4j.spring.web.context;

/**
 * Settings used when building a {@link ProblemContext} for incoming requests.
 *
 * <p>Provides access to infrastructure configuration such as the HTTP header name that carries a
 * trace identifier. Implementations are typically backed by external configuration (e.g. Spring
 * Boot properties) and are expected to be thread-safe.
 */
public interface ProblemContextSettings {

  /**
   * Returns the name of the HTTP header that contains a trace / correlation ID.
   *
   * <p>The trace ID (if present) may be injected into generated Problem responses (e.g. via
   * placeholders in instance/type override templates) and echoed back to clients to aid in log
   * correlation and diagnostics.
   *
   * @return the tracing header name, or {@code null} if tracing is disabled / not configured
   */
  String getTracingHeaderName();
}
