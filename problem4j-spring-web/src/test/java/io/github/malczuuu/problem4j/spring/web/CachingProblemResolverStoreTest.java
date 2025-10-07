package io.github.malczuuu.problem4j.spring.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.malczuuu.problem4j.spring.web.format.IdentityProblemFormat;
import io.github.malczuuu.problem4j.spring.web.resolver.AbstractProblemResolver;
import io.github.malczuuu.problem4j.spring.web.resolver.ProblemResolver;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CachingProblemResolverStoreTest {

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
    ProblemResolver baseMapping = new TestResolver(MyBaseException.class);
    ProblemResolver subMapping = new TestResolver(MySubException.class);
    ProblemResolver otherMapping = new TestResolver(OtherException.class);
    store = new CachingProblemResolverStore(List.of(baseMapping, subMapping, otherMapping));
  }

  @Test
  void testExactMatch() {
    Optional<ProblemResolver> result = store.resolver(MySubException.class);

    assertTrue(result.isPresent());
    assertEquals(MySubException.class, result.get().getExceptionClass());
  }

  @Test
  void testClosestSuperclassMatch() {
    Optional<ProblemResolver> result = store.resolver(MySubSubException.class);

    assertTrue(result.isPresent());
    assertEquals(MySubException.class, result.get().getExceptionClass());
  }

  @Test
  void testBaseClassMatch() {
    Optional<ProblemResolver> result = store.resolver(MyBaseException.class);

    assertTrue(result.isPresent());
    assertEquals(MyBaseException.class, result.get().getExceptionClass());
  }

  @Test
  void testNoMatchForUnrelatedException() {
    Optional<ProblemResolver> result = store.resolver(Exception.class);

    assertTrue(result.isEmpty());
  }

  @Test
  void givenProblemResolverStore_whenFindingMapping_thenCacheIsUsed() {
    AtomicInteger counter = new AtomicInteger(0);
    ProblemResolver resolver =
        new TestResolver(MyBaseException.class) {
          @Override
          public Class<? extends Exception> getExceptionClass() {
            counter.incrementAndGet();
            return super.getExceptionClass();
          }
        };

    CachingProblemResolverStore store = new CachingProblemResolverStore(List.of(resolver));

    Optional<ProblemResolver> firstLookup = store.resolver(MyBaseException.class);

    assertTrue(firstLookup.isPresent(), "resolver should be present");
    assertEquals(1, counter.get(), "counter should increment once");

    Optional<ProblemResolver> secondLookup = store.resolver(MyBaseException.class);

    assertTrue(secondLookup.isPresent(), "resolver should still be present");
    assertEquals(1, counter.get(), "counter should not increment again");
  }

  @Test
  void givenNoMatchingMapping_whenFindingMapping_thenSearchIsCached() {
    AtomicInteger computeCounter = new AtomicInteger(0);

    CachingProblemResolverStore store =
        new CachingProblemResolverStore(List.of()) {
          @Override
          protected Optional<ProblemResolver> computeResolver(Class<? extends Exception> clazz) {
            computeCounter.incrementAndGet();
            return super.computeResolver(clazz);
          }
        };

    Optional<ProblemResolver> first = store.resolver(MyBaseException.class);
    assertTrue(first.isEmpty(), "should be empty for unmapped exception");

    Optional<ProblemResolver> second = store.resolver(MyBaseException.class);
    assertTrue(second.isEmpty(), "should still be empty");
    assertEquals(1, computeCounter.get(), "computeMapping() should be called only once");
  }

  private static class DummyResolver extends AbstractProblemResolver {
    DummyResolver(Class<? extends Exception> clazz) {
      super(clazz, new IdentityProblemFormat());
    }
  }

  @Test
  void whenManyThreadsLookupSameException_thenComputeResolverRunsOnce() throws Exception {
    AtomicInteger computeCounter = new AtomicInteger(0);

    DummyResolver resolver = new DummyResolver(IOException.class);

    CachingProblemResolverStore store =
        new CachingProblemResolverStore(List.of(resolver)) {
          @Override
          protected Optional<ProblemResolver> computeResolver(Class<? extends Exception> clazz) {
            computeCounter.incrementAndGet();
            try {
              // simulate slow computation
              Thread.sleep(200);
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
            }
            return super.computeResolver(clazz);
          }
        };

    int threadCount = 20;
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);

    List<Future<Optional<ProblemResolver>>> futures;
    try {
      List<Callable<Optional<ProblemResolver>>> tasks = new ArrayList<>();

      for (int i = 0; i < threadCount; i++) {
        tasks.add(() -> store.resolver(IOException.class));
      }

      futures = executor.invokeAll(tasks);
    } finally {
      executor.shutdown();
    }

    assertTrue(executor.awaitTermination(2, TimeUnit.SECONDS));

    for (Future<Optional<ProblemResolver>> f : futures) {
      assertTrue(f.get().isPresent());
      assertSame(resolver, f.get().get(), "all threads should get the same instance");
    }

    assertEquals(1, computeCounter.get(), "computeMapping() should run exactly once");
  }
}
