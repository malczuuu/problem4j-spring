package io.github.malczuuu.problem4j.spring.web;

import io.github.malczuuu.problem4j.spring.web.resolver.ProblemResolver;
import java.util.Optional;

/**
 * Registry that provides access to {@link ProblemResolver} instances based on exception types.
 *
 * <p>Implementations are responsible for locating the most specific resolver for a given {@code
 * Exception} class, typically by checking class assignability and caching results to improve
 * performance.
 */
public interface ProblemResolverStore {

  /**
   * Finds the most specific {@link ProblemResolver} for the given exception class.
   *
   * <p>This method searches the store for all resolvers whose keys are assignable from the provided
   * exception class. If multiple resolvers match, it returns the one closest in the class hierarchy
   * (i.e., the most specific resolver). If no resolver is found, an empty {@link Optional} is
   * returned. Results are cached to optimize repeated lookups for the same class.
   *
   * @param clazz the exception class for which to find a resolver
   * @return an {@link Optional} containing the most specific {@link ProblemResolver} if found,
   *     otherwise an empty {@link Optional}
   */
  Optional<ProblemResolver> findResolver(Class<? extends Exception> clazz);
}
