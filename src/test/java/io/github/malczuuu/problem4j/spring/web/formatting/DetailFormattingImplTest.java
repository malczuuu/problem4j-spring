package io.github.malczuuu.problem4j.spring.web.formatting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class DetailFormattingImplTest {

  @Test
  void givenLowercaseFormat_whenFormatting_thenReturnsLowercase() {
    DetailFormattingImpl formatting = new DetailFormattingImpl("lowercase");

    String result = formatting.format("TeSt StrIng");

    assertThat(result).isEqualTo("test string");
  }

  @Test
  void givenUppercaseFormat_whenFormatting_thenReturnsUppercase() {
    DetailFormattingImpl formatting = new DetailFormattingImpl("uppercase");

    String result = formatting.format("TeSt StrIng");

    assertThat(result).isEqualTo("TEST STRING");
  }

  @Test
  void givenUnknownFormat_whenFormatting_thenReturnsUnchanged() {
    DetailFormattingImpl formatting = new DetailFormattingImpl("something-else");

    String result = formatting.format("TeSt StrIng");

    assertThat(result).isEqualTo("TeSt StrIng");
  }

  @Test
  void givenEmptyString_whenFormatting_thenReturnsEmpty() {
    DetailFormattingImpl formatting = new DetailFormattingImpl("uppercase");

    String result = formatting.format("");

    assertThat(result).isEqualTo("");
  }

  @Test
  void givenNullInput_whenFormatting_thenThrowsNpe() {
    DetailFormattingImpl formatting = new DetailFormattingImpl("uppercase");

    assertThrows(NullPointerException.class, () -> formatting.format(null));
  }
}
