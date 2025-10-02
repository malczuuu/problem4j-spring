package io.github.malczuuu.problem4j.spring.web.format;

import io.github.malczuuu.problem4j.spring.web.ProblemProperties;
import java.util.Locale;
import org.springframework.util.StringUtils;

/**
 * Defines problem {@code "detail"} field formatting based on {@code problem4j.detail-format}
 * property.
 *
 * <pre>{@code
 * lowercase     "Validation failed"  will be transformed to  "validation failed"
 * capitalized   "Validation failed"  will be transformed to  "Validation failed"
 * uppercase     "Validation failed"  will be transformed to  "VALIDATION FAILED"
 *
 * (any other)   "Validation failed"  will be transformed to  "Validation failed"
 * }</pre>
 */
public class SimpleDetailFormat implements DetailFormat {

  private final String detailFormat;

  public SimpleDetailFormat(String detailFormat) {
    this.detailFormat = detailFormat;
  }

  @Override
  public String format(String detail) {
    if (detailFormat == null || !StringUtils.hasText(detail)) {
      return detail;
    }
    return switch (detailFormat.toLowerCase()) {
      case ProblemProperties.DetailFormats.LOWERCASE -> detail.toLowerCase(Locale.ROOT);
      case ProblemProperties.DetailFormats.CAPITALIZED -> capitalize(detail);
      case ProblemProperties.DetailFormats.UPPERCASE -> detail.toUpperCase(Locale.ROOT);
      default -> detail;
    };
  }

  private String capitalize(String detail) {
    return Character.toTitleCase(detail.charAt(0)) + detail.substring(1);
  }
}
