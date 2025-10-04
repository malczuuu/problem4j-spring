package io.github.malczuuu.problem4j.spring.web;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Configuration properties for Problem Details integration.
 *
 * <p>These properties can be set under the {@code problem4j.*} prefix.
 */
@ConfigurationProperties(prefix = "problem4j")
public class ProblemProperties {

  private final String detailFormat;
  private final String tracingHeaderName;
  private final String instanceOverride;

  /**
   * Creates a new instance.
   *
   * @param detailFormat the format for the {@code "detail"} field
   */
  public ProblemProperties(
      @DefaultValue(DetailFormats.CAPITALIZED) String detailFormat,
      String tracingHeaderName,
      String instanceOverride) {
    this.detailFormat = detailFormat;
    this.tracingHeaderName = tracingHeaderName;
    this.instanceOverride = instanceOverride;
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
  public String getTracingHeaderName() {
    return tracingHeaderName;
  }

  /**
   * Returns the configured instance override.
   *
   * <p>This value may contain the special placeholder {@code {traceId}}, which will be replaced at
   * runtime with the current trace identifier from the {@link ProblemContext}. If no override is
   * configured, this method may return {@code null}.
   *
   * <p>This is useful if {@code instance} field will not be known while throwing {@code
   * ProblemException} (or {@code @ProblemMapping}-annotated exception). Setting this configuration,
   * along with {@link #tracingHeaderName} will enable this feature.
   *
   * @return the configured instance override string, or {@code null} if not set
   */
  public String getInstanceOverride() {
    return instanceOverride;
  }

  /** Supported values for {@code detailFormat}. */
  public static final class DetailFormats {

    /** All detail messages in lowercase. */
    public static final String LOWERCASE = "lowercase";

    /** Detail messages with the first letter capitalized. */
    public static final String CAPITALIZED = "capitalized";

    /** All detail messages in uppercase. */
    public static final String UPPERCASE = "uppercase";

    private DetailFormats() {}
  }
}
