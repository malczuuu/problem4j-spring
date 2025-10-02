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

  /**
   * Creates a new instance.
   *
   * @param detailFormat the format for the {@code "detail"} field
   */
  public ProblemProperties(@DefaultValue(DetailFormats.CAPITALIZED) String detailFormat) {
    this.detailFormat = detailFormat;
  }

  /**
   * Returns the configured format for the {@code "detail"} field.
   *
   * @return the detail format
   */
  public String getDetailFormat() {
    return detailFormat;
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
