package io.github.malczuuu.problem4j.spring.web.format;

public class IdentityProblemFormat implements ProblemFormat {

  @Override
  public String formatDetail(String detail) {
    return detail;
  }
}
