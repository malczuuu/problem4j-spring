package io.github.malczuuu.problem4j.spring.web.mapping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.spring.web.CachingExceptionMappingStore;
import io.github.malczuuu.problem4j.spring.web.ExceptionMappingStore;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;

class CachingExceptionMappingStoreTest {

  private static class MyBaseException extends Exception {}

  private static class MySubException extends MyBaseException {}

  private static class MySubSubException extends MySubException {}

  private static class OtherException extends Exception {}

  private static class TestMapping implements ExceptionMapping {

    private final Class<? extends Exception> clazz;

    TestMapping(Class<? extends Exception> clazz) {
      this.clazz = clazz;
    }

    @Override
    public Class<? extends Exception> getExceptionClass() {
      return clazz;
    }

    @Override
    public Problem map(Exception ex, HttpHeaders headers, HttpStatusCode status) {
      // irrelevant
      return null;
    }
  }

  private ExceptionMappingStore registry;

  @BeforeEach
  void setUp() {
    ExceptionMapping baseMapping = new TestMapping(MyBaseException.class);
    ExceptionMapping subMapping = new TestMapping(MySubException.class);
    ExceptionMapping otherMapping = new TestMapping(OtherException.class);
    registry = new CachingExceptionMappingStore(List.of(baseMapping, subMapping, otherMapping));
  }

  @Test
  void testExactMatch() {
    Optional<ExceptionMapping> result = registry.findMapping(MySubException.class);

    assertTrue(result.isPresent());
    assertEquals(MySubException.class, result.get().getExceptionClass());
  }

  @Test
  void testClosestSuperclassMatch() {
    Optional<ExceptionMapping> result = registry.findMapping(MySubSubException.class);

    assertTrue(result.isPresent());
    assertEquals(MySubException.class, result.get().getExceptionClass());
  }

  @Test
  void testBaseClassMatch() {
    Optional<ExceptionMapping> result = registry.findMapping(MyBaseException.class);

    assertTrue(result.isPresent());
    assertEquals(MyBaseException.class, result.get().getExceptionClass());
  }

  @Test
  void testNoMatchForUnrelatedException() {
    Optional<ExceptionMapping> result = registry.findMapping(Exception.class);

    assertTrue(result.isEmpty());
  }
}
