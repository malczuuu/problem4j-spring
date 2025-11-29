package io.github.malczuuu.problem4j.spring.web;

import io.github.malczuuu.problem4j.spring.web.context.ProblemContextSettings;
import io.github.malczuuu.problem4j.spring.web.processor.PostProcessorSettings;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Configuration properties for Problem Details integration.
 *
 * <p>These properties can be set under the {@code problem4j.*} prefix.
 */
@ConfigurationProperties(prefix = "problem4j")
public class ProblemProperties implements ProblemContextSettings, PostProcessorSettings {

  private final boolean enabled;
  private final String detailFormat;
  private final String tracingHeaderName;
  private final String typeOverride;
  private final String instanceOverride;

  private final ResolverCaching resolverCaching;

  /**
   * Creates a new instance.
   *
   * @param enabled whether problem handling is enabled
   * @param detailFormat format for the "detail" field (one of {@link DetailFormat#LOWERCASE},
   *     {@link DetailFormat#CAPITALIZED}, {@link DetailFormat#UPPERCASE})
   * @param tracingHeaderName name of the HTTP header carrying a trace ID (nullable)
   * @param instanceOverride template for overriding the "instance" field; may contain
   *     "{context.traceId}" placeholder (nullable)
   * @param resolverCaching caching for resolver lookups ({@link CachingProblemResolverStore});
   *     defaults to {@link ResolverCaching#createDefault()}
   */
  public ProblemProperties(
      @DefaultValue("true") boolean enabled,
      @DefaultValue(DetailFormat.CAPITALIZED) String detailFormat,
      String tracingHeaderName,
      String typeOverride,
      String instanceOverride,
      ResolverCaching resolverCaching) {
    this.enabled = enabled;
    this.detailFormat = detailFormat;
    this.tracingHeaderName = tracingHeaderName;
    this.typeOverride = typeOverride;
    this.instanceOverride = instanceOverride;
    this.resolverCaching =
        resolverCaching != null ? resolverCaching : ResolverCaching.createDefault();
  }

  /**
   * Indicates whether problem handling is currently enabled.
   *
   * @return {@code true} if problem handling is enabled; {@code false} otherwise
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * Returns the configured format for the {@code "detail"} field.
   *
   * @return the detail format
   */
  public String getDetailFormat() {
    return detailFormat;
  }

  /**
   * Returns the name of the HTTP header used for tracing requests.
   *
   * <p>This header provides the trace identifier that can be injected into responses. When combined
   * with {@link #instanceOverride}, the trace ID may be used to dynamically populate the {@code
   * instance} field of a {@code Problem} response.
   *
   * <p>If no header name is configured, this method may return {@code null}.
   *
   * @return the tracing header name, or {@code null} if not set
   */
  @Override
  public String getTracingHeaderName() {
    return tracingHeaderName;
  }

  /**
   * Returns the configured type override.
   *
   * <p>This value defines a fixed or templated {@code type} URI to be used for all problems
   * processed by the post-processor, replacing the original problem type if present. The value may
   * include special placeholders that will be dynamically replaced at runtime:
   *
   * <ul>
   *   <li>{@code {problem.type}} - replaced with the original problem’s type URI
   *   <li>{@code {context.traceId}} - replaced with the current trace identifier from the {@code
   *       ProblemContext}
   * </ul>
   *
   * <p>This allows flexible configuration of problem types depending on context or trace
   * information. If no override is configured, this method may return {@code null}, and the
   * original problem type will be preserved.
   *
   * @return the configured type override string, or {@code null} if not set
   * @see io.github.malczuuu.problem4j.spring.web.context.ProblemContext
   */
  @Override
  public String getTypeOverride() {
    return typeOverride;
  }

  /**
   * Returns the configured instance override.
   *
   * <p>This value may contain special placeholders that will be replaced at runtime with contextual
   * or problem-specific data:
   *
   * <ul>
   *   <li>{@code {problem.instance}} - replaced with the original problem’s instance URI
   *   <li>{@code {context.traceId}} - replaced with the current trace identifier from the {@code
   *       ProblemContext}
   * </ul>
   *
   * <p>This is useful if the {@code instance} field cannot be determined when throwing a {@code
   * ProblemException} (or an exception annotated with {@code @ProblemMapping}). Setting this
   * configuration, along with {@link #tracingHeaderName}, enables this feature.
   *
   * @return the configured instance override string, or {@code null} if not set
   * @see io.github.malczuuu.problem4j.spring.web.context.ProblemContext
   */
  @Override
  public String getInstanceOverride() {
    return instanceOverride;
  }

  /**
   * Returns the caching configuration.
   *
   * @return caching settings
   */
  public ResolverCaching getResolverCaching() {
    return resolverCaching;
  }

  /**
   * Caching configuration for ({@link CachingProblemResolverStore}).
   *
   * <p>Controls whether resolver lookup caching is enabled and its maximum size.
   */
  public static class ResolverCaching {

    public static final boolean DEFAULT_ENABLED = false;
    public static final String DEFAULT_ENABLED_VALUE = "false";

    public static final int DEFAULT_MAX_CACHE_SIZE = -1;
    public static final String DEFAULT_MAX_CACHE_SIZE_VALUE = "-1";

    private static ResolverCaching createDefault() {
      return new ResolverCaching(DEFAULT_ENABLED, DEFAULT_MAX_CACHE_SIZE);
    }

    private final boolean enabled;
    private final int maxCacheSize;

    /**
     * Creates a new caching configuration.
     *
     * @param enabled whether caching is enabled
     * @param maxCacheSize maximum number of cached entries (-1 or 0 means unbounded)
     */
    public ResolverCaching(
        @DefaultValue(DEFAULT_ENABLED_VALUE) boolean enabled,
        @DefaultValue(DEFAULT_MAX_CACHE_SIZE_VALUE) int maxCacheSize) {
      this.enabled = enabled;
      this.maxCacheSize = maxCacheSize;
    }

    /**
     * Returns whether caching is enabled.
     *
     * @return true if enabled
     */
    public boolean isEnabled() {
      return enabled;
    }

    /**
     * Returns the maximum cache size.
     *
     * @return maximum entries; -1 (or non-positive) means unbounded
     */
    public int getMaxCacheSize() {
      return maxCacheSize;
    }
  }

  /** Supported values for {@code detailFormat}. */
  public static final class DetailFormat {

    /** All detail messages in lowercase. */
    public static final String LOWERCASE = "lowercase";

    /** Detail messages with the first letter capitalized. */
    public static final String CAPITALIZED = "capitalized";

    /** All detail messages in uppercase. */
    public static final String UPPERCASE = "uppercase";

    private DetailFormat() {}
  }
}
