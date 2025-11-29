package io.github.malczuuu.problem4j.spring.web;

import io.github.malczuuu.problem4j.spring.web.resolver.ProblemResolver;
import io.github.malczuuu.problem4j.spring.web.util.ClassDistanceEvaluation;
import io.github.malczuuu.problem4j.spring.web.util.GraphClassDistanceEvaluation;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * {@link ProblemResolverStore} implementation evaluates resolved based on class and its
 * inheritance.
 *
 * <p>Resolvers are matched to exceptions by assignability, preferring the most specific exception
 * type.
 */
public abstract class AbstractProblemResolverStore implements ProblemResolverStore {

  private final Map<Class<? extends Exception>, ProblemResolver> resolvers;
  private final ClassDistanceEvaluation classDistanceEvaluation;

  /**
   * Creates a new store initialized with the given resolvers.
   *
   * @param problemResolvers list of available {@link ProblemResolver} instances
   * @throws NullPointerException if any resolver or its exception class is {@code null}
   */
  public AbstractProblemResolverStore(List<ProblemResolver> problemResolvers) {
    this(problemResolvers, new GraphClassDistanceEvaluation());
  }

  /**
   * Creates a new store initialized with the given resolvers and a specific class distance
   * evaluation strategy.
   *
   * @param problemResolvers list of available {@link ProblemResolver} instances
   * @param classDistanceEvaluation the strategy used to evaluate the distance between exception
   *     classes (e.g., when finding the best resolver for a specific exception type).
   * @throws NullPointerException if any resolver or its exception class is {@code null}
   */
  public AbstractProblemResolverStore(
      List<ProblemResolver> problemResolvers, ClassDistanceEvaluation classDistanceEvaluation) {
    Map<Class<? extends Exception>, ProblemResolver> copy = new HashMap<>(problemResolvers.size());
    problemResolvers.forEach(
        resolver -> copy.put(resolver.getExceptionClass(), Objects.requireNonNull(resolver)));
    this.resolvers = Map.copyOf(copy);
    this.classDistanceEvaluation = classDistanceEvaluation;
  }

  /**
   * Returns a {@link ProblemResolver} for the given exception class.
   *
   * <p>This method searches for resolvers whose exception type is assignable from the given class,
   * selecting the most specific match.
   *
   * @param clazz exception class to resolve
   * @return an {@link Optional} containing the matching resolver, or empty if none found
   */
  @Override
  public Optional<ProblemResolver> findResolver(Class<? extends Exception> clazz) {
    List<ProblemResolver> candidates = new ArrayList<>();
    for (Map.Entry<Class<? extends Exception>, ProblemResolver> entry : resolvers.entrySet()) {
      if (entry.getKey().isAssignableFrom(clazz)) {
        candidates.add(entry.getValue());
      }
    }

    return candidates.stream().min(Comparator.comparingInt(r -> calculateDistance(r, clazz)));
  }

  private int calculateDistance(ProblemResolver resolver, Class<? extends Exception> clazz) {
    return classDistanceEvaluation.calculate(clazz, resolver.getExceptionClass());
  }
}
