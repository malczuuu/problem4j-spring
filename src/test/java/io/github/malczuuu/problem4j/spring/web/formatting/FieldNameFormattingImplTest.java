package io.github.malczuuu.problem4j.spring.web.formatting;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class FieldNameFormattingImplTest {

  @Test
  void givenNullStrategy_whenFormat_thenReturnsOriginalField() {
    FieldNameFormatting formatting = new FieldNameFormattingImpl(null);

    String result = formatting.format("myFieldName");

    assertThat(result).isEqualTo("myFieldName");
  }

  @ParameterizedTest(name = "{0} applied to {1} -> {2}")
  @CsvSource({
    "SNAKE_CASE, myFieldName, my_field_name",
    "UPPER_CAMEL_CASE, myFieldName, MyFieldName",
    "KEBAB_CASE, myFieldName, my-field-name",
    "LOWER_CASE, myFieldName, myfieldname",
    "UNKNOWN, myFieldName, myFieldName"
  })
  void givenStrategyAndField_whenFormat_thenReturnsExpected(
      String strategy, String input, String expected) {
    FieldNameFormatting formatting = new FieldNameFormattingImpl(strategy);

    String result = formatting.format(input);

    assertThat(result).isEqualTo(expected);
  }

  @Test
  void givenEmptyString_whenFormat_thenReturnsEmptyString() {
    FieldNameFormatting formatting = new FieldNameFormattingImpl("SNAKE_CASE");

    String result = formatting.format("");

    assertThat(result).isEmpty();
  }

  @Test
  void givenSingleCharacter_whenFormatWithUpperCamelCase_thenReturnsUppercaseCharacter() {
    FieldNameFormatting formatting = new FieldNameFormattingImpl("UPPER_CAMEL_CASE");

    String result = formatting.format("x");

    assertThat(result).isEqualTo("X");
  }

  @Test
  void givenAcronymField_whenFormatWithSnakeCase_thenReturnsExpected() {
    FieldNameFormatting formatting = new FieldNameFormattingImpl("SNAKE_CASE");

    String result = formatting.format("URLValue");

    assertThat(result).isEqualTo("urlvalue");
  }

  @Test
  void givenNumbers_whenFormatWithSnakeCase_thenReturnsExpected() {
    FieldNameFormatting formatting = new FieldNameFormattingImpl("SNAKE_CASE");

    String result = formatting.format("field1Name");

    assertThat(result).isEqualTo("field1_name");
  }

  @Test
  void givenAlreadySnakeCaseField_whenFormat_thenReturnsSameField() {
    FieldNameFormatting formatting = new FieldNameFormattingImpl("SNAKE_CASE");

    String result = formatting.format("my_field_name");

    assertThat(result).isEqualTo("my_field_name");
  }
}
