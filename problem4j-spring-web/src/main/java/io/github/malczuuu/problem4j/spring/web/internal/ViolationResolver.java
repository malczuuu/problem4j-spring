package io.github.malczuuu.problem4j.spring.web.internal;

import static io.github.malczuuu.problem4j.spring.web.internal.MethodParameterSupport.findParameterName;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.ERRORS_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.IS_NOT_VALID_ERROR;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.VALIDATION_FAILED_DETAIL;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.spring.web.format.ProblemFormat;
import io.github.malczuuu.problem4j.spring.web.model.Violation;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.method.MethodValidationResult;
import org.springframework.validation.method.ParameterValidationResult;

/**
 * <b>For internal use only.</b>
 *
 * <p>This class is intended for internal use within the {@code problem4j-spring-*} libraries and
 * should not be used directly by external applications. The API may change or be removed without
 * notice.
 *
 * <p><b>Use at your own risk.</b>
 *
 * @implNote This is an internal API and may change at any time.
 * @see ApiStatus.Internal
 */
@ApiStatus.Internal
public class ViolationResolver {

  private final ProblemFormat problemFormat;

  /**
   * Creates a new resolver that converts validation results into {@link ProblemBuilder}s.
   *
   * @param problemFormat formatting strategy used for the problem {@code detail} field
   */
  public ViolationResolver(ProblemFormat problemFormat) {
    this.problemFormat = problemFormat;
  }

  /**
   * Builds a {@link ProblemBuilder} for a {@link MethodValidationResult} produced by method /
   * parameter validation (e.g. {@code @Validated} on a controller). Each parameter violation is
   * mapped to a {@link Violation} whose name is the resolved method parameter name and message is
   * the constraint message.
   *
   * <p>The resulting builder has:
   *
   * <ul>
   *   <li>{@code detail} set to a formatted {@code VALIDATION_FAILED_DETAIL}
   *   <li>{@code errors} extension (key {@code ProblemSupport#ERRORS_EXTENSION}) containing a list
   *       of {@link Violation}
   * </ul>
   *
   * @param e aggregated method validation result (must not be {@code null})
   * @return a builder pre-populated with validation detail and violations extension
   * @see io.github.malczuuu.problem4j.spring.web.util.ProblemSupport
   */
  public ProblemBuilder from(MethodValidationResult e) {
    List<Violation> violations = new ArrayList<>();

    for (ParameterValidationResult result : e.getValueResults()) {
      String fieldName = findParameterName(result.getMethodParameter()).orElse(null);
      result
          .getResolvableErrors()
          .forEach(error -> violations.add(new Violation(fieldName, error.getDefaultMessage())));
    }
    return Problem.builder()
        .detail(problemFormat.formatDetail(VALIDATION_FAILED_DETAIL))
        .extension(ERRORS_EXTENSION, violations);
  }

  /**
   * Builds a {@link ProblemBuilder} from a Spring {@link BindingResult} (e.g. produced when binding
   * a {@code @ModelAttribute} fails or when {@code @Valid} detects field / global errors). Field
   * errors are translated into {@link Violation}s keyed by field name; global errors use {@code
   * null} as the field name.
   *
   * <p>The resulting builder has:
   *
   * <ul>
   *   <li>{@code detail} set to a formatted {@code VALIDATION_FAILED_DETAIL}
   *   <li>{@code errors} extension (key {@code ProblemSupport#ERRORS_EXTENSION}) containing a list
   *       of {@link Violation}
   * </ul>
   *
   * @param bindingResult the binding/validation result to convert (must not be {@code null})
   * @return a builder pre-populated with validation detail and violations extension
   * @see io.github.malczuuu.problem4j.spring.web.util.ProblemSupport#ERRORS_EXTENSION
   */
  public ProblemBuilder from(BindingResult bindingResult) {
    ArrayList<Violation> errors = new ArrayList<>();
    bindingResult.getFieldErrors().forEach(f -> errors.add(resolveFieldError(f)));
    bindingResult.getGlobalErrors().forEach(g -> errors.add(resolveGlobalError(g)));
    return Problem.builder()
        .detail(problemFormat.formatDetail(VALIDATION_FAILED_DETAIL))
        .extension(ERRORS_EXTENSION, errors);
  }

  /**
   * {@code isBindingFailure() == true} usually means that there was a failure in creation of object
   * from values taken out of request. Most common one is type mismatch between
   * {@code @ModelAttribute}-annotated argument and one of its values. Consider running {@code
   * WebExchangeBindExceptionWebFluxTest} or {@code MethodArgumentNotValidExceptionMvcTest} to debug
   * this feature.
   */
  private Violation resolveFieldError(FieldError error) {
    if (error.isBindingFailure()) {
      return new Violation(error.getField(), IS_NOT_VALID_ERROR);
    } else {
      return new Violation(error.getField(), error.getDefaultMessage());
    }
  }

  private Violation resolveGlobalError(ObjectError error) {
    return new Violation(null, error.getDefaultMessage());
  }
}
