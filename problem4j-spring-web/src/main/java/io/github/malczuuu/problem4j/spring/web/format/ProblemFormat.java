package io.github.malczuuu.problem4j.spring.web.format;

/**
 * Defines a contract for formatting problem detail field and property names (mostly before they are
 * included in a {@code Problem} response).
 *
 * <p>Implementations can customize how details and property names are presented — for example, by
 * applying localization, case formatting, or message templating.
 */
public interface ProblemFormat {

  /** Format {@code detail} field of {@code Problem} model. */
  String formatDetail(String detail);
}
