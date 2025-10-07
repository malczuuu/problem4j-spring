package io.github.malczuuu.problem4j.spring.web.resolver;

import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.KIND_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.PROPERTY_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.TYPE_MISMATCH_DETAIL;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.format.ProblemFormat;
import java.util.Locale;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

public class TypeMismatchResolver extends AbstractProblemResolver {

  public TypeMismatchResolver(ProblemFormat problemFormat) {
    super(TypeMismatchException.class, problemFormat);
  }

  @Override
  public ProblemBuilder resolveBuilder(
      ProblemContext context, Exception ex, HttpHeaders headers, HttpStatusCode status) {
    ProblemBuilder builder =
        Problem.builder()
            .status(ProblemStatus.BAD_REQUEST)
            .detail(formatDetail(TYPE_MISMATCH_DETAIL));

    TypeMismatchException ex1 = (TypeMismatchException) ex;

    String property = ex1.getPropertyName();
    String kind =
        ex1.getRequiredType() != null
            ? ex1.getRequiredType().getSimpleName().toLowerCase(Locale.ROOT)
            : null;

    // could happen in some early 3.0.x versions of Spring Boot, cannot add tests for it as newer
    // versions assign it to propertyName in constructor
    if (property == null && ex instanceof MethodArgumentTypeMismatchException ex2) {
      property = ex2.getName();
    }

    if (property != null) {
      builder = builder.extension(PROPERTY_EXTENSION, property);
    }
    if (kind != null) {
      builder = builder.extension(KIND_EXTENSION, kind);
    }
    return builder;
  }
}
