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
import org.junit.jupiter.api.Test;

class CachingProblemResolverStoreTest {

  private static class TestException extends Exception {}

  private static class TestResolver extends AbstractProblemResolver {
    TestResolver(Class<? extends Exception> clazz) {
      super(clazz);
    }
  }

  @Test
  void givenProblemResolverStore_whenFindingResolver_thenCacheIsUsed() {
    AtomicInteger counter = new AtomicInteger(0);
    ProblemResolver resolver =
        new TestResolver(TestException.class) {
          @Override
          public Class<? extends Exception> getExceptionClass() {
            counter.incrementAndGet();
            return super.getExceptionClass();
          }
        };

    CachingProblemResolverStore store =
        new CachingProblemResolverStore(new HashMapProblemResolverStore(List.of(resolver)));

    Optional<ProblemResolver> firstLookup = store.findResolver(TestException.class);

    assertTrue(firstLookup.isPresent(), "resolver should be present");
    assertEquals(1, counter.get(), "counter should increment once");

    Optional<ProblemResolver> secondLookup = store.findResolver(TestException.class);

    assertTrue(secondLookup.isPresent(), "resolver should still be present");
    assertEquals(1, counter.get(), "counter should not increment again");
  }

  @Test
  void givenNoMatchingResolver_whenFindingResolver_thenSearchIsCached() {
    AtomicInteger computeCounter = new AtomicInteger(0);

    CachingProblemResolverStore store =
        new CachingProblemResolverStore(
            clazz -> {
              computeCounter.incrementAndGet();
              return Optional.empty();
            });

    Optional<ProblemResolver> first = store.findResolver(TestException.class);
    assertTrue(first.isEmpty(), "should be empty for unmapped exception");

    Optional<ProblemResolver> second = store.findResolver(TestException.class);
    assertTrue(second.isEmpty(), "should still be empty");
    assertEquals(1, computeCounter.get(), "computeResolver() should be called only once");
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
        new CachingProblemResolverStore(
            clazz -> {
              computeCounter.incrementAndGet();
              return Optional.of(resolver);
            });

    int threadCount = 20;
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);

    List<Future<Optional<ProblemResolver>>> futures;
    try {
      List<Callable<Optional<ProblemResolver>>> tasks = new ArrayList<>();

      for (int i = 0; i < threadCount; i++) {
        tasks.add(() -> store.findResolver(IOException.class));
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

    assertEquals(1, computeCounter.get(), "computeResolver() should run exactly once");
  }
}
