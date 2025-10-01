package io.github.malczuuu.problem4j.spring.web.mapping;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.formatting.DetailFormatting;
import io.github.malczuuu.problem4j.spring.web.formatting.FieldNameFormatting;
import io.github.malczuuu.problem4j.spring.web.validation.Violation;
import java.util.ArrayList;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

final class MethodArgumentNotValidMapping implements ExceptionMapping {

  private final DetailFormatting detailFormatting;
  private final FieldNameFormatting fieldNameFormatting;

  MethodArgumentNotValidMapping(
      DetailFormatting detailFormatting, FieldNameFormatting fieldNameFormatting) {
    this.detailFormatting = detailFormatting;
    this.fieldNameFormatting = fieldNameFormatting;
  }

  @Override
  public Class<MethodArgumentNotValidException> getExceptionClass() {
    return MethodArgumentNotValidException.class;
  }

  @Override
  public Problem map(Exception ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    MethodArgumentNotValidException e = (MethodArgumentNotValidException) ex;
    return from(e.getBindingResult()).status(ProblemStatus.BAD_REQUEST).build();
  }

  private ProblemBuilder from(BindingResult bindingResult) {
    ArrayList<Violation> details = new ArrayList<>();
    bindingResult
        .getFieldErrors()
        .forEach(
            f ->
                details.add(
                    new Violation(
                        fieldNameFormatting.format(f.getField()), f.getDefaultMessage())));
    bindingResult
        .getGlobalErrors()
        .forEach(g -> details.add(new Violation(null, g.getDefaultMessage())));
    return Problem.builder()
        .detail(detailFormatting.format("Validation failed"))
        .extension("errors", details);
  }
}
