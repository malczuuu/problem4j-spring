package io.github.malczuuu.problem4j.spring.web.format;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

import io.github.malczuuu.problem4j.spring.web.ProblemProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class SimpleDetailFormatTest {

  @Test
  void givenLowercaseFormat_whenFormatting_thenReturnsLowercase() {
    SimpleDetailFormat formatting =
        new SimpleDetailFormat(ProblemProperties.DetailFormats.LOWERCASE);

    String result = formatting.format("TeSt StrIng");

    assertThat(result).isEqualTo("test string");
  }

  @Test
  void givenUppercaseFormat_whenFormatting_thenReturnsUppercase() {
    SimpleDetailFormat formatting =
        new SimpleDetailFormat(ProblemProperties.DetailFormats.UPPERCASE);

    String result = formatting.format("TeSt StrIng");

    assertThat(result).isEqualTo("TEST STRING");
  }

  @Test
  void givenCapitalizedFormat_whenFormatting_thenReturnsUppercase() {
    SimpleDetailFormat formatting =
        new SimpleDetailFormat(ProblemProperties.DetailFormats.CAPITALIZED);

    String result = formatting.format("test string");

    assertThat(result).isEqualTo("Test string");
  }

  @Test
  void givenUnknownFormat_whenFormatting_thenReturnsUnchanged() {
    SimpleDetailFormat formatting = new SimpleDetailFormat("something-else");

    String result = formatting.format("TeSt StrIng");

    assertThat(result).isEqualTo("TeSt StrIng");
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        ProblemProperties.DetailFormats.LOWERCASE,
        ProblemProperties.DetailFormats.UPPERCASE,
        ProblemProperties.DetailFormats.CAPITALIZED,
        ""
      })
  @NullSource
  void givenEmptyString_whenFormatting_thenReturnsEmpty(String detailFormat) {
    SimpleDetailFormat formatting = new SimpleDetailFormat(detailFormat);

    String result = formatting.format("");

    assertThat(result).isEqualTo("");
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        ProblemProperties.DetailFormats.LOWERCASE,
        ProblemProperties.DetailFormats.UPPERCASE,
        ProblemProperties.DetailFormats.CAPITALIZED,
        ""
      })
  @NullSource
  void givenNullInput_whenFormatting_thenReturnsNull(String detailFormat) {
    SimpleDetailFormat formatting = new SimpleDetailFormat(detailFormat);

    assertNull(formatting.format(null));
  }
}
