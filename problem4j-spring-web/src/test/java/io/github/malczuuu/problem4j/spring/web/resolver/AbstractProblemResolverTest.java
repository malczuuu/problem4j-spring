package io.github.malczuuu.problem4j.spring.web.resolver;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.format.IdentityProblemFormat;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;

class AbstractProblemResolverTest {

  public static Stream<Arguments> exceptions() {
    return Stream.of(Arguments.of(IllegalArgumentException.class, IllegalStateException.class));
  }

  @ParameterizedTest
  @MethodSource("exceptions")
  void givenAnyMapping_shouldReturnExceptionClass(Class<? extends Exception> clazz) {
    AbstractProblemResolver resolver =
        new AbstractProblemResolver(clazz, new IdentityProblemFormat()) {
          @Override
          public ProblemBuilder resolveBuilder(
              ProblemContext context, Exception ex, HttpHeaders headers, HttpStatusCode status) {
            return Problem.builder().status(ProblemStatus.INTERNAL_SERVER_ERROR);
          }
        };

    Class<? extends Exception> exceptionClass = resolver.getExceptionClass();

    assertThat(exceptionClass).isEqualTo(clazz);
  }
}
