package io.github.malczuuu.problem4j.spring.web;

import io.github.malczuuu.problem4j.spring.web.resolver.ProblemResolver;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link ProblemResolverStore} implementation that caches resolver lookups for better performance.
 *
 * <p>Resolvers are matched to exceptions by assignability, preferring the most specific exception
 * type. Results are stored in an internal cache to avoid repeated resolution.
 */
public class CachingProblemResolverStore implements ProblemResolverStore {

  private final Map<Class<? extends Exception>, ProblemResolver> resolvers;
  private final ConcurrentHashMap<Class<? extends Exception>, Optional<ProblemResolver>> cache =
      new ConcurrentHashMap<>();

  /**
   * Creates a new store initialized with the given resolvers.
   *
   * @param problemResolvers list of available {@link ProblemResolver} instances
   * @throws NullPointerException if any resolver or its exception class is {@code null}
   */
  public CachingProblemResolverStore(List<ProblemResolver> problemResolvers) {
    Map<Class<? extends Exception>, ProblemResolver> copy = new HashMap<>(problemResolvers.size());
    problemResolvers.forEach(
        resovler -> copy.put(resovler.getExceptionClass(), Objects.requireNonNull(resovler)));
    this.resolvers = Map.copyOf(copy);
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
    return cache.computeIfAbsent(clazz, this::computeResolver);
  }

  /**
   * Computes a matching {@link ProblemResolver} for the given exception class.
   *
   * <p>This method searches for resolvers whose exception type is assignable from the given class,
   * selecting the most specific match.
   *
   * @param clazz exception class to resolve
   * @return an {@link Optional} containing the best matching resolver, or empty if none found
   */
  protected Optional<ProblemResolver> computeResolver(Class<? extends Exception> clazz) {
    List<ProblemResolver> candidates = new ArrayList<>();
    for (Map.Entry<Class<? extends Exception>, ProblemResolver> entry : resolvers.entrySet()) {
      if (entry.getKey().isAssignableFrom(clazz)) {
        candidates.add(entry.getValue());
      }
    }

    return candidates.stream()
        .min(Comparator.comparingInt(m -> distance(clazz, m.getExceptionClass())));
  }

  /**
   * Calculates the inheritance distance between two exception types.
   *
   * <p>The distance represents how far the resolver’s exception type is from the thrown type in the
   * class hierarchy.
   *
   * @param thrown the actual thrown exception class
   * @param resolverKey the resolver’s exception class
   * @return number of superclass steps between the two types, or {@link Integer#MAX_VALUE} if not
   *     assignable
   */
  private static int distance(Class<?> thrown, Class<?> resolverKey) {
    if (!resolverKey.isAssignableFrom(thrown)) {
      return Integer.MAX_VALUE;
    }
    int distance = 0;
    Class<?> current = thrown;
    while (current != null && !current.equals(resolverKey)) {
      current = current.getSuperclass();
      distance++;
    }
    return distance;
  }
}
