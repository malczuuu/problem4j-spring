package io.github.malczuuu.problem4j.spring.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.spring.web.mapping.ExceptionMapping;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
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

  private ExceptionMappingStore store;

  @BeforeEach
  void setUp() {
    ExceptionMapping baseMapping = new TestMapping(MyBaseException.class);
    ExceptionMapping subMapping = new TestMapping(MySubException.class);
    ExceptionMapping otherMapping = new TestMapping(OtherException.class);
    store = new CachingExceptionMappingStore(List.of(baseMapping, subMapping, otherMapping));
  }

  @Test
  void testExactMatch() {
    Optional<ExceptionMapping> result = store.findMapping(MySubException.class);

    assertTrue(result.isPresent());
    assertEquals(MySubException.class, result.get().getExceptionClass());
  }

  @Test
  void testClosestSuperclassMatch() {
    Optional<ExceptionMapping> result = store.findMapping(MySubSubException.class);

    assertTrue(result.isPresent());
    assertEquals(MySubException.class, result.get().getExceptionClass());
  }

  @Test
  void testBaseClassMatch() {
    Optional<ExceptionMapping> result = store.findMapping(MyBaseException.class);

    assertTrue(result.isPresent());
    assertEquals(MyBaseException.class, result.get().getExceptionClass());
  }

  @Test
  void testNoMatchForUnrelatedException() {
    Optional<ExceptionMapping> result = store.findMapping(Exception.class);

    assertTrue(result.isEmpty());
  }

  @Test
  void givenExceptionMappingStore_whenFindingMapping_thenCacheIsUsed() {
    AtomicInteger counter = new AtomicInteger(0);
    ExceptionMapping mapping =
        new TestMapping(MyBaseException.class) {
          @Override
          public Class<? extends Exception> getExceptionClass() {
            counter.incrementAndGet();
            return super.getExceptionClass();
          }
        };

    CachingExceptionMappingStore store = new CachingExceptionMappingStore(List.of(mapping));

    Optional<ExceptionMapping> firstLookup = store.findMapping(MyBaseException.class);

    assertTrue(firstLookup.isPresent(), "mapping should be present");
    assertEquals(1, counter.get(), "counter should increment once");

    Optional<ExceptionMapping> secondLookup = store.findMapping(MyBaseException.class);

    assertTrue(secondLookup.isPresent(), "mapping should still be present");
    assertEquals(1, counter.get(), "counter should not increment again");
  }
}
