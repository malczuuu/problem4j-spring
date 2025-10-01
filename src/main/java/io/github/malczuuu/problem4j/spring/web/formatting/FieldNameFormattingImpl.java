package io.github.malczuuu.problem4j.spring.web.formatting;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;

class FieldNameFormattingImpl implements FieldNameFormatting {

  private final String propertyNamingStrategy;

  FieldNameFormattingImpl(String propertyNamingStrategy) {
    this.propertyNamingStrategy = propertyNamingStrategy;
  }

  @Override
  public String format(String fieldName) {
    if (propertyNamingStrategy == null) {
      return fieldName;
    }

    return switch (propertyNamingStrategy.toUpperCase()) {
      case "SNAKE_CASE" ->
          ((PropertyNamingStrategies.SnakeCaseStrategy) PropertyNamingStrategies.SNAKE_CASE)
              .translate(fieldName);
      case "UPPER_CAMEL_CASE" ->
          ((PropertyNamingStrategies.UpperCamelCaseStrategy)
                  PropertyNamingStrategies.UPPER_CAMEL_CASE)
              .translate(fieldName);
      case "KEBAB_CASE" ->
          ((PropertyNamingStrategies.KebabCaseStrategy) PropertyNamingStrategies.KEBAB_CASE)
              .translate(fieldName);
      case "LOWER_CASE" ->
          ((PropertyNamingStrategies.LowerCaseStrategy) PropertyNamingStrategies.LOWER_CASE)
              .translate(fieldName);
      default -> fieldName;
    };
  }
}
