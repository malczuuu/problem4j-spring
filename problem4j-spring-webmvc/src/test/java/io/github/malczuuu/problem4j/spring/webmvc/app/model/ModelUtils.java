package io.github.malczuuu.problem4j.spring.webmvc.app.model;

final class ModelUtils {

  static int safeParseInt(String text) {
    try {
      return Integer.parseInt(text);
    } catch (NumberFormatException e) {
      return -1;
    }
  }

  private ModelUtils() {}
}
