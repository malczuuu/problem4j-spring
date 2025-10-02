package io.github.malczuuu.problem4j.spring.web.format;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

/**
 * Defines problem extension field name formatting based on {@code
 * spring.jackson.property-naming-strategy}.
 *
 * <pre>{@code
 * UPPER_CAMEL_CASE   "myFieldName"  will be transformed to  "MyFieldName",
 * LOWER_CAMEL_CASE   "myFieldName"  will be transformed to  "myFieldName",
 * SNAKE_CASE         "myFieldName"  will be transformed to  "my_field_name",
 * UPPER_SNAKE_CASE   "myFieldName"  will be transformed to  "MY_FIELD_NAME",
 * LOWER_CASE         "myFieldName"  will be transformed to  "myfieldname",
 * KEBAB_CASE         "myFieldName"  will be transformed to  "my-field-name",
 * LOWER_DOT_CASE     "myFieldName"  will be transformed to  "my.field.name",
 *
 * (any other)        "myFieldName"  will be transformed to  "myFieldName"
 * }</pre>
 */
public class JacksonPropertyNameFormat implements PropertyNameFormat {

  private final String propertyNamingStrategy;

  public JacksonPropertyNameFormat(String propertyNamingStrategy) {
    this.propertyNamingStrategy = propertyNamingStrategy;
  }

  @Override
  public String format(String fieldName) {
    if (propertyNamingStrategy == null) {
      return fieldName;
    }

    return switch (propertyNamingStrategy.toUpperCase()) {
      case "UPPER_CAMEL_CASE" -> translate(PropertyNamingStrategies.UPPER_CAMEL_CASE, fieldName);
      case "LOWER_CAMEL_CASE" -> translate(PropertyNamingStrategies.LOWER_CAMEL_CASE, fieldName);
      case "SNAKE_CASE" -> translate(PropertyNamingStrategies.SNAKE_CASE, fieldName);
      case "UPPER_SNAKE_CASE" -> translate(PropertyNamingStrategies.UPPER_SNAKE_CASE, fieldName);
      case "LOWER_CASE" -> translate(PropertyNamingStrategies.LOWER_CASE, fieldName);
      case "KEBAB_CASE" -> translate(PropertyNamingStrategies.KEBAB_CASE, fieldName);
      case "LOWER_DOT_CASE" -> translate(PropertyNamingStrategies.LOWER_DOT_CASE, fieldName);
      default -> fieldName;
    };
  }

  private String translate(PropertyNamingStrategy strategy, String fieldName) {
    if (strategy instanceof PropertyNamingStrategies.NamingBase namingBase) {
      return namingBase.translate(fieldName);
    }
    return fieldName;
  }
}
