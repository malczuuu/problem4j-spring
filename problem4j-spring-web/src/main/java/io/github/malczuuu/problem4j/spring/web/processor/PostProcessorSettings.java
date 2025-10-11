package io.github.malczuuu.problem4j.spring.web.processor;

/**
 * Defines configuration settings used by {@link ProblemPostProcessor} implementations to control
 * how problem responses are modified before being returned to the client.
 *
 * <p>Implementations of this interface typically provide values from application configuration (for
 * example, {@code problem4j.type-override} and {@code problem4j.instance-override}) and may include
 * runtime placeholders that are resolved during post-processing.
 *
 * <p>These settings allow applications to dynamically customize problem types and instances to
 * match organizational or tracing conventions.
 */
public interface PostProcessorSettings {

  /**
   * Returns the configured override template for the {@code type} field of a problem.
   *
   * <p>The value may include placeholders such as {@code {problem.type}}, which will be replaced at
   * runtime.
   *
   * @return the configured type override template, or {@code null} if not set
   */
  String getTypeOverride();

  /**
   * Returns the configured override template for the {@code instance} field of a problem.
   *
   * <p>The value may include placeholders such as {@code {problem.instance}} or {@code
   * {context.traceId}}, which will be replaced at runtime.
   *
   * @return the configured instance override template, or {@code null} if not set
   */
  String getInstanceOverride();
}
