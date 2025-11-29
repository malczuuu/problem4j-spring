package io.github.malczuuu.problem4j.spring.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.malczuuu.problem4j.spring.web.resolver.AbstractProblemResolver;
import io.github.malczuuu.problem4j.spring.web.resolver.ProblemResolver;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AbstractProblemResolverStoreTest {

  private static class MyBaseException extends Exception {}

  private static class MySubException extends MyBaseException {}

  private static class MySubSubException extends MySubException {}

  private static class OtherException extends Exception {}

  private static class TestResolver extends AbstractProblemResolver {
    TestResolver(Class<? extends Exception> clazz) {
      super(clazz);
    }
  }

  private ProblemResolverStore store;

  @BeforeEach
  void setUp() {
    ProblemResolver baseResolver = new TestResolver(MyBaseException.class);
    ProblemResolver subResolver = new TestResolver(MySubException.class);
    ProblemResolver otherResolver = new TestResolver(OtherException.class);
    store = new AbstractProblemResolverStore(List.of(baseResolver, subResolver, otherResolver)) {};
  }

  @Test
  void testExactMatch() {
    Optional<ProblemResolver> result = store.findResolver(MySubException.class);

    assertTrue(result.isPresent());
    assertEquals(MySubException.class, result.get().getExceptionClass());
  }

  @Test
  void testClosestSuperclassMatch() {
    Optional<ProblemResolver> result = store.findResolver(MySubSubException.class);

    assertTrue(result.isPresent());
    assertEquals(MySubException.class, result.get().getExceptionClass());
  }

  @Test
  void testBaseClassMatch() {
    Optional<ProblemResolver> result = store.findResolver(MyBaseException.class);

    assertTrue(result.isPresent());
    assertEquals(MyBaseException.class, result.get().getExceptionClass());
  }

  @Test
  void testNoMatchForUnrelatedException() {
    Optional<ProblemResolver> result = store.findResolver(Exception.class);

    assertTrue(result.isEmpty());
  }
}
