package io.github.malczuuu.problem4j.spring.web;

import io.github.malczuuu.problem4j.spring.web.resolver.ProblemResolver;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link ProblemResolverStore} implementation that caches resolver lookups for better performance.
 *
 * <p>Results are stored in an internal cache to avoid repeated resolution.
 */
public class CachingProblemResolverStore implements ProblemResolverStore {

  private final ProblemResolverStore delegate;

  private final ConcurrentHashMap<Class<? extends Exception>, Optional<ProblemResolver>> cache =
      new ConcurrentHashMap<>();

  /**
   * Creates a new store initialized with the given resolvers.
   *
   * @param delegate the delegate store to use for resolver lookups
   * @throws NullPointerException if any resolver or its exception class is {@code null}
   */
  public CachingProblemResolverStore(ProblemResolverStore delegate) {
    this.delegate = delegate;
  }

  /**
   * Returns a {@link ProblemResolver} for the given exception class.
   *
   * <p>Results are cached for subsequent lookups.
   *
   * @param clazz exception class to resolve
   * @return an {@link Optional} containing the matching resolver, or empty if none found
   */
  @Override
  public Optional<ProblemResolver> findResolver(Class<? extends Exception> clazz) {
    return cache.computeIfAbsent(clazz, delegate::findResolver);
  }
}
