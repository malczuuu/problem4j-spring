package io.github.malczuuu.problem4j.spring.web.formatting;

import io.github.malczuuu.problem4j.spring.web.ProblemProperties;
import java.util.Locale;

class DetailFormattingImpl implements DetailFormatting {

  private final String detailFormat;

  DetailFormattingImpl(String detailFormat) {
    this.detailFormat = detailFormat;
  }

  @Override
  public String format(String detail) {
    if (detailFormat == null) {
      return detail;
    }
    return switch (detailFormat.toLowerCase()) {
      case ProblemProperties.DetailFormat.LOWERCASE -> detail.toLowerCase(Locale.ROOT);
      case ProblemProperties.DetailFormat.UPPERCASE -> detail.toUpperCase(Locale.ROOT);
      default -> detail;
    };
  }
}
