package io.github.problem4j.spring.web.parameter;

import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.method.MethodValidationResult;
import org.springframework.validation.method.ParameterValidationResult;

/** Default implementation of {@link MethodValidationResultSupport}. */
public class DefaultMethodValidationResultSupport implements MethodValidationResultSupport {

  private final MethodParameterSupport methodParameterSupport;

  /** Uses {@link DefaultMethodParameterSupport} as the default {@link MethodParameterSupport}. */
  public DefaultMethodValidationResultSupport() {
    this(new DefaultMethodParameterSupport());
  }

  /**
   * Creates a new instance using provided {@link MethodParameterSupport}.
   *
   * @param methodParameterSupport the {@link MethodParameterSupport} implementation to use
   */
  public DefaultMethodValidationResultSupport(MethodParameterSupport methodParameterSupport) {
    this.methodParameterSupport = methodParameterSupport;
  }

  /**
   * Builds a {@link Violation}s list from a {@link MethodValidationResult} produced by method /
   * parameter validation (e.g. {@code @Validated} on a controller). Each parameter violation is
   * mapped to a {@code Violation} whose name is the resolved method parameter name and message is
   * the constraint message.
   *
   * @param result aggregated method validation result (must not be {@code null})
   * @return list of violations extracted from the validation result
   */
  @Override
  public List<Violation> fetchViolations(MethodValidationResult result) {
    List<Violation> violations = new ArrayList<>();
    for (ParameterValidationResult valueResult : result.getValueResults()) {
      String fieldName =
          methodParameterSupport.findParameterName(valueResult.getMethodParameter()).orElse(null);
      valueResult
          .getResolvableErrors()
          .forEach(error -> violations.add(new Violation(fieldName, error.getDefaultMessage())));
    }
    return violations;
  }
}
