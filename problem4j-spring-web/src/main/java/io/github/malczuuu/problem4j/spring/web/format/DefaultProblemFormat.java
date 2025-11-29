package io.github.malczuuu.problem4j.spring.web.format;

import io.github.malczuuu.problem4j.spring.web.ProblemProperties;
import java.util.Locale;
import org.springframework.util.StringUtils;

/**
 * Default implementation of {@link ProblemFormat} that applies text transformations to problem
 * details according to a configured format.
 *
 * <p>Property {@link ProblemProperties#getDetailFormat()} determines formatting behaviour -
 * lowercase, capitalized, or uppercase.
 *
 * <p>This class is typically registered automatically as a Spring bean, but can also be
 * instantiated directly.
 *
 * @see ProblemFormat
 * @see ProblemProperties.DetailFormat
 */
public class DefaultProblemFormat implements ProblemFormat {

  private final String detailFormat;

  public DefaultProblemFormat(String detailFormat) {
    this.detailFormat = detailFormat;
  }

  /**
   * Applies formatting to the given {@code "detail"} text according to following rules.
   *
   * <pre>{@code
   * lowercase     "Validation failed"  will be transformed to  "validation failed"
   * capitalized   "Validation failed"  will be transformed to  "Validation failed"
   * uppercase     "Validation failed"  will be transformed to  "VALIDATION FAILED"
   *
   * (any other)   "Validation failed"  will be transformed to  "Validation failed"
   * }</pre>
   *
   * @param detail the raw text, may be {@code null}
   * @return the formatted text, or {@code null} if input was {@code null}
   */
  @Override
  public String formatDetail(String detail) {
    if (!StringUtils.hasLength(detail)) {
      return detail;
    }
    return switch (detailFormat.toLowerCase()) {
      case ProblemProperties.DetailFormat.LOWERCASE -> detail.toLowerCase(Locale.ROOT);
      case ProblemProperties.DetailFormat.CAPITALIZED -> capitalize(detail);
      case ProblemProperties.DetailFormat.UPPERCASE -> detail.toUpperCase(Locale.ROOT);
      default -> detail;
    };
  }

  private String capitalize(String detail) {
    return Character.toTitleCase(detail.charAt(0)) + detail.substring(1);
  }
}
