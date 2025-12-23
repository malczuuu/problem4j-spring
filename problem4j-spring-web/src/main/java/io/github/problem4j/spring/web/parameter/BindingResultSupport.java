package io.github.problem4j.spring.web.parameter;

import java.util.List;
import org.springframework.validation.BindingResult;

/** Support for converting Spring {@link BindingResult}s into {@code ProblemBuilder}s. */
public interface BindingResultSupport {

  /**
   * Builds a {@link Violation}s list from a Spring {@link BindingResult} (e.g. produced when
   * binding a {@code @ModelAttribute} fails or when {@code @Valid} detects field / global errors).
   * Field errors are translated into {@link Violation}s keyed by field name; global errors use
   * {@code null} as the field name.
   *
   * @param result the binding/validation result to convert (must not be {@code null})
   * @return list of violations extracted from the binding result
   */
  List<Violation> fetchViolations(BindingResult result);
}
