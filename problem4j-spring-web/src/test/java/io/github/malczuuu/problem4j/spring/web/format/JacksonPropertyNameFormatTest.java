package io.github.malczuuu.problem4j.spring.web.format;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class JacksonPropertyNameFormatTest {

  @Test
  void givenNullStrategy_whenFormat_thenReturnsOriginalField() {
    PropertyNameFormat formatting = new JacksonPropertyNameFormat(null);

    String result = formatting.format("myFieldName");

    assertThat(result).isEqualTo("myFieldName");
  }

  @ParameterizedTest(name = "{0} applied to {1} -> {2}")
  @CsvSource({
    "UPPER_CAMEL_CASE, myFieldName, MyFieldName",
    "LOWER_CAMEL_CASE, myFieldName, myFieldName",
    "SNAKE_CASE,       myFieldName, my_field_name",
    "UPPER_SNAKE_CASE, myFieldName, MY_FIELD_NAME",
    "LOWER_CASE,       myFieldName, myfieldname",
    "KEBAB_CASE,       myFieldName, my-field-name",
    "LOWER_DOT_CASE,   myFieldName, my.field.name",
    "UNKNOWN,          myFieldName, myFieldName"
  })
  void givenStrategyAndField_whenFormat_thenReturnsExpected(
      String strategy, String input, String expected) {
    PropertyNameFormat formatting = new JacksonPropertyNameFormat(strategy);

    String result = formatting.format(input);

    assertThat(result).isEqualTo(expected);
  }

  @Test
  void givenEmptyString_whenFormat_thenReturnsEmptyString() {
    PropertyNameFormat formatting = new JacksonPropertyNameFormat("SNAKE_CASE");

    String result = formatting.format("");

    assertThat(result).isEmpty();
  }

  @Test
  void givenSingleCharacter_whenFormatWithUpperCamelCase_thenReturnsUppercaseCharacter() {
    PropertyNameFormat formatting = new JacksonPropertyNameFormat("UPPER_CAMEL_CASE");

    String result = formatting.format("x");

    assertThat(result).isEqualTo("X");
  }

  @Test
  void givenAcronymField_whenFormatWithSnakeCase_thenReturnsExpected() {
    PropertyNameFormat formatting = new JacksonPropertyNameFormat("SNAKE_CASE");

    String result = formatting.format("URLValue");

    assertThat(result).isEqualTo("urlvalue");
  }

  @Test
  void givenNumbers_whenFormatWithSnakeCase_thenReturnsExpected() {
    PropertyNameFormat formatting = new JacksonPropertyNameFormat("SNAKE_CASE");

    String result = formatting.format("field1Name");

    assertThat(result).isEqualTo("field1_name");
  }

  @Test
  void givenAlreadySnakeCaseField_whenFormat_thenReturnsSameField() {
    PropertyNameFormat formatting = new JacksonPropertyNameFormat("SNAKE_CASE");

    String result = formatting.format("my_field_name");

    assertThat(result).isEqualTo("my_field_name");
  }
}
