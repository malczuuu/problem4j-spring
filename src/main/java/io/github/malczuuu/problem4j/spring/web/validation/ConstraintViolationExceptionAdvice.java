package io.github.malczuuu.problem4j.spring.web.validation;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.spring.web.formatting.DetailFormatting;
import io.github.malczuuu.problem4j.spring.web.formatting.FieldNameFormatting;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

/**
 * Handles {@link ConstraintViolationException} thrown during validation.
 *
 * <p>Transforms validation constraint violations into a {@link Problem} response with HTTP status
 * {@link HttpStatus#BAD_REQUEST}.
 *
 * <p>Behavior:
 *
 * <ul>
 *   <li>Extracts property paths and violation messages from each {@link ConstraintViolation}.
 *   <li>Formats field names using {@link FieldNameFormatting}.
 *   <li>Formats overall detail message using {@link DetailFormatting}.
 *   <li>Includes all validation errors in the {@code errors} extension property of the {@link
 *       Problem} object.
 * </ul>
 */
@RestControllerAdvice
public class ConstraintViolationExceptionAdvice {

  private final DetailFormatting detailFormatting;
  private final FieldNameFormatting fieldNameFormatting;

  public ConstraintViolationExceptionAdvice(
      DetailFormatting detailFormatting, FieldNameFormatting fieldNameFormatting) {
    this.detailFormatting = detailFormatting;
    this.fieldNameFormatting = fieldNameFormatting;
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Problem> handleConstraintViolationException(
      ConstraintViolationException ex, WebRequest request) {
    HttpStatus status = HttpStatus.BAD_REQUEST;
    List<Violation> errors =
        ex.getConstraintViolations().stream()
            .map(
                violation ->
                    new Violation(
                        fieldNameFormatting.format(fetchViolationProperty(violation)),
                        violation.getMessage()))
            .toList();

    Problem problem =
        Problem.builder()
            .title(status.getReasonPhrase())
            .status(status.value())
            .detail(detailFormatting.format("Validation failed"))
            .extension("errors", errors)
            .build();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

    return new ResponseEntity<>(problem, headers, status);
  }

  private String fetchViolationProperty(ConstraintViolation<?> violation) {
    if (violation.getPropertyPath() == null) {
      return "";
    }

    String lastElement = null;
    for (Path.Node node : violation.getPropertyPath()) {
      lastElement = node.getName();
    }

    return lastElement != null ? lastElement : "";
  }
}
