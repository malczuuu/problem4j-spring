package io.github.problem4j.spring.web.parameter;

import io.github.problem4j.core.ProblemBuilder;
import java.util.List;
import org.springframework.validation.method.MethodValidationResult;

/** Support for converting Spring {@link MethodValidationResult}s into {@link ProblemBuilder}s. */
public interface MethodValidationResultSupport {

  /**
   * Builds a {@link Violation}s list from a {@link MethodValidationResult} produced by method /
   * parameter validation (e.g. {@code @Validated} on a controller). Each parameter violation is
   * mapped to a {@code Violation} whose name is the resolved method parameter name and message is
   * the constraint message.
   *
   * @param result aggregated method validation result (must not be {@code null})
   * @return list of violations extracted from the validation result
   */
  List<Violation> fetchViolations(MethodValidationResult result);
}
