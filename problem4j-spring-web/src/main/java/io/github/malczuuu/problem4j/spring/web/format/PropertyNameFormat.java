package io.github.malczuuu.problem4j.spring.web.format;

/**
 * In order to format problem extensions to match ordinary fields, this component interface defining
 * such method.
 *
 * <p>For example, for given custom API, its creators may want all fields to be on {@code
 * snake_case} format.
 *
 * <p>The default implementation in {@link JacksonPropertyNameFormat} relies on {@code
 * spring.jackson.property-naming-strategy} property and transforms your extensions to match your
 * configured format.
 *
 * @see JacksonPropertyNameFormat
 */
public interface PropertyNameFormat {

  /**
   * Applies formatting to the given field name.
   *
   * @param fieldName the original name, may be {@code null}
   * @return the formatted name, or {@code null} if input was {@code null}
   */
  String format(String fieldName);
}
