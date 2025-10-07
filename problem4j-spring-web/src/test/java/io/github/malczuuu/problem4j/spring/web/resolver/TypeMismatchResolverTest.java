package io.github.malczuuu.problem4j.spring.web.resolver;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.format.IdentityProblemFormat;
import io.github.malczuuu.problem4j.spring.web.util.ProblemSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;

class TypeMismatchResolverTest {

  private TypeMismatchResolver typeMismatchMapping;

  @BeforeEach
  void beforeEach() {
    typeMismatchMapping = new TypeMismatchResolver(new IdentityProblemFormat());
  }

  @Test
  void givenExceptionWithParameterNameAndType_shouldReturnProblemWithAll() {
    TypeMismatchException ex = new TypeMismatchException("42", Integer.class);
    ex.initPropertyName("age");

    Problem problem =
        typeMismatchMapping
            .resolve(ProblemContext.empty(), ex, new HttpHeaders(), HttpStatusCode.valueOf(400))
            .build();

    assertThat(problem)
        .isEqualTo(
            Problem.builder()
                .status(ProblemStatus.BAD_REQUEST)
                .detail(ProblemSupport.TYPE_MISMATCH_DETAIL)
                .extension(ProblemSupport.PROPERTY_EXTENSION, "age")
                .extension(ProblemSupport.KIND_EXTENSION, "integer")
                .build());
  }

  @Test
  void givenExceptionWithParameterType_shouldReturnProblemWithTypeOnly() {
    TypeMismatchException ex = new TypeMismatchException("42", Integer.class);

    Problem problem =
        typeMismatchMapping
            .resolve(ProblemContext.empty(), ex, new HttpHeaders(), HttpStatusCode.valueOf(400))
            .build();

    assertThat(problem)
        .isEqualTo(
            Problem.builder()
                .status(ProblemStatus.BAD_REQUEST)
                .detail(ProblemSupport.TYPE_MISMATCH_DETAIL)
                .extension(ProblemSupport.KIND_EXTENSION, "integer")
                .build());
  }

  @Test
  void givenExceptionWithParameterName_shouldReturnProblemWithNameOnly() {
    TypeMismatchException ex = new TypeMismatchException("value", null);
    ex.initPropertyName("field");

    Problem problem =
        typeMismatchMapping
            .resolve(ProblemContext.empty(), ex, new HttpHeaders(), HttpStatusCode.valueOf(400))
            .build();

    assertThat(problem)
        .isEqualTo(
            Problem.builder()
                .status(ProblemStatus.BAD_REQUEST)
                .detail(ProblemSupport.TYPE_MISMATCH_DETAIL)
                .extension(ProblemSupport.PROPERTY_EXTENSION, "field")
                .build());
  }
}
