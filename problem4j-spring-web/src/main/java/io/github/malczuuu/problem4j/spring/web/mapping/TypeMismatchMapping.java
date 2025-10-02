package io.github.malczuuu.problem4j.spring.web.mapping;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.format.DetailFormat;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;

public class TypeMismatchMapping implements ExceptionMapping {

  private final DetailFormat detailFormat;

  public TypeMismatchMapping(DetailFormat detailFormat) {
    this.detailFormat = detailFormat;
  }

  @Override
  public Class<TypeMismatchException> getExceptionClass() {
    return TypeMismatchException.class;
  }

  @Override
  public Problem map(Exception ex, HttpHeaders headers, HttpStatusCode status) {
    TypeMismatchException e = (TypeMismatchException) ex;
    ProblemBuilder builder =
        Problem.builder()
            .status(ProblemStatus.BAD_REQUEST)
            .detail(detailFormat.format("Type mismatch"));

    if (e.getPropertyName() != null) {
      builder = builder.extension("property", e.getPropertyName());
    }
    if (e.getRequiredType() != null) {
      builder = builder.extension("kind", e.getRequiredType().getSimpleName().toLowerCase());
    }
    return builder.build();
  }
}
