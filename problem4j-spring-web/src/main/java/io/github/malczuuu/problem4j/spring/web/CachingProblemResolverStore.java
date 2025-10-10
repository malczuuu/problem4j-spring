package io.github.malczuuu.problem4j.spring.web;

import io.github.malczuuu.problem4j.spring.web.resolver.ProblemResolver;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * {@link ProblemResolverStore} implementation that caches resolver lookups for better performance.
 *
 * <p>Results are stored in an internal cache to avoid repeated resolution.
 */
public class CachingProblemResolverStore implements ProblemResolverStore {

  private final ProblemResolverStore delegate;
  private final int maxCacheSize;

  private final Map<Class<? extends Exception>, Optional<ProblemResolver>> cache;

  /**
   * Creates a new store initialized with the given delegate and an unbounded cache.
   *
   * @param delegate the delegate store to use for resolver lookups
   */
  public CachingProblemResolverStore(ProblemResolverStore delegate) {
    this(delegate, -1);
  }

  /**
   * Creates a new store with an LRU cache limited to maxEntries.
   *
   * @param maxCacheSize maximum number of cached entries (fallbacks to {@link Integer#MAX_VALUE} on
   *     invalid values)
   * @param delegate the delegate store to use for resolver lookups
   */
  public CachingProblemResolverStore(ProblemResolverStore delegate, int maxCacheSize) {
    this.delegate = delegate;
    this.maxCacheSize = maxCacheSize > 0 ? maxCacheSize : Integer.MAX_VALUE;

    // 16 is the default initial capacity of HashMap;
    // 0.75f is the default load factor;
    // accessOrder=true - the last accessed entry is moved to the end of the underlying list
    this.cache =
        new LinkedHashMap<>(16, 0.75f, true) {
          @Override
          protected boolean removeEldestEntry(
              Map.Entry<Class<? extends Exception>, Optional<ProblemResolver>> eldest) {
            return size() > CachingProblemResolverStore.this.maxCacheSize;
          }
        };
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
    synchronized (cache) {
      return cache.computeIfAbsent(clazz, delegate::findResolver);
    }
  }
}
