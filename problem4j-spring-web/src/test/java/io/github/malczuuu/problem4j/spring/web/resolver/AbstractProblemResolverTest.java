package io.github.malczuuu.problem4j.spring.web.resolver;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class AbstractProblemResolverTest {

  public static Stream<Arguments> exceptions() {
    return Stream.of(Arguments.of(IllegalArgumentException.class, IllegalStateException.class));
  }

  @ParameterizedTest
  @MethodSource("exceptions")
  void givenAnyMapping_shouldReturnExceptionClass(Class<? extends Exception> clazz) {
    AbstractProblemResolver resolver = new AbstractProblemResolver(clazz) {};

    Class<? extends Exception> exceptionClass = resolver.getExceptionClass();

    assertThat(exceptionClass).isEqualTo(clazz);
  }
}
