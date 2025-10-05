package io.github.malczuuu.problem4j.spring.web.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.spring.web.format.IdentityProblemFormat;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;

class AbstractExceptionMappingTest {

  public static Stream<Arguments> exceptions() {
    return Stream.of(Arguments.of(IllegalArgumentException.class, IllegalStateException.class));
  }

  @ParameterizedTest
  @MethodSource("exceptions")
  void givenAnyMapping_shouldReturnExceptionClass(Class<? extends Exception> clazz) {
    AbstractExceptionMapping mapping =
        new AbstractExceptionMapping(clazz, new IdentityProblemFormat()) {
          @Override
          public Problem map(Exception ex, HttpHeaders headers, HttpStatusCode status) {
            // irrelevant
            return null;
          }
        };

    Class<? extends Exception> exceptionClass = mapping.getExceptionClass();

    assertThat(exceptionClass).isEqualTo(clazz);
  }
}
