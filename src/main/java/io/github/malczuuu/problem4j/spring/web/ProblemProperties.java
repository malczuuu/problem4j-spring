package io.github.malczuuu.problem4j.spring.web;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "problem4j")
public class ProblemProperties {

  private final String detailFormat;

  public ProblemProperties(@DefaultValue(DetailFormat.CAPITALIZED) String detailFormat) {
    this.detailFormat = detailFormat;
  }

  public String getDetailFormat() {
    return detailFormat;
  }

  public static final class DetailFormat {

    public static final String LOWERCASE = "lowercase";
    public static final String CAPITALIZED = "capitalized";
    public static final String UPPERCASE = "uppercase";

    private DetailFormat() {}
  }
}
