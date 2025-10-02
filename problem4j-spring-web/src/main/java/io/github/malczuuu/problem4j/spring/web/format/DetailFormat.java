package io.github.malczuuu.problem4j.spring.web.format;

/**
 * Default problem {@code "detail"} fields provided by {@code problem4j-spring} libraries are
 * capitalized. For unifying style of target custom applications, it's possible to configure how
 * {@code "detail"} field is printed using implementation of this interface.
 *
 * <p>The default implementation in {@link SimpleDetailFormat} relies on {@code
 * problem4j.detail-format} property.
 *
 * @see SimpleDetailFormat
 */
public interface DetailFormat {

  /**
   * Applies formatting to the given {@code "detail"} text.
   *
   * @param detail the raw text, may be {@code null}
   * @return the formatted text, or {@code null} if input was {@code null}
   */
  String format(String detail);
}
