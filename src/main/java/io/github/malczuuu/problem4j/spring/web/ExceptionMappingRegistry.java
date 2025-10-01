package io.github.malczuuu.problem4j.spring.web;

import io.github.malczuuu.problem4j.spring.web.mapping.ExceptionMapping;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for managing per-exception {@link
 * io.github.malczuuu.problem4j.spring.web.mapping.ExceptionMapping} instances.
 *
 * <p>Instead of constantly overriding new methods in a central exception handler, this registry
 * allows each Spring-handled exception to have its own dedicated mapping. The main handler (e.g.,
 * {@code handleExceptionInternal}) delegates to the appropriate mapping and returns a {@link
 * io.github.malczuuu.problem4j.core.Problem} response.
 *
 * <p>Benefits of this design:
 *
 * <ul>
 *   <li>Supports modular exception handling, avoiding large, monolithic handlers.
 *   <li>Relying on {@link org.springframework.boot.autoconfigure.condition.ConditionalOnClass},
 *       exception mappings for unknown classes will never be instantiated, therefore this registry
 *       will return empty {@link Optional}.
 * </ul>
 *
 * <p><strong>Note:</strong> This registry is intended only for Spring framework exceptions, not for
 * custom application exceptions.
 *
 * @see io.github.malczuuu.problem4j.spring.web.mapping.ExceptionMappingConfiguration
 */
class ExceptionMappingRegistry {

  private final Map<Class<? extends Exception>, ExceptionMapping> mappings;

  private final Map<Class<? extends Exception>, ExceptionMapping> cache = new ConcurrentHashMap<>();

  ExceptionMappingRegistry(List<ExceptionMapping> exceptionMappings) {
    this.mappings = new HashMap<>(exceptionMappings.size());
    exceptionMappings.forEach(mapping -> mappings.put(mapping.getExceptionClass(), mapping));
  }

  /**
   * Finds the most specific {@link ExceptionMapping} for the given exception class.
   *
   * <p>This method searches the registry for all mappings whose keys are assignable from the
   * provided exception class. If multiple mappings match, it returns the one closest in the class
   * hierarchy (i.e., the most specific mapping). If no mapping is found, an empty {@link Optional}
   * is returned. Results are cached to optimize repeated lookups for the same class.
   *
   * @param clazz the exception class for which to find a mapping
   * @return an {@link Optional} containing the most specific {@link ExceptionMapping} if found,
   *     otherwise an empty {@link Optional}
   */
  Optional<ExceptionMapping> findMapping(Class<? extends Exception> clazz) {
    ExceptionMapping cached = cache.get(clazz);
    if (cached != null) {
      return Optional.of(cached);
    }

    List<ExceptionMapping> candidates = new ArrayList<>();
    mappings.forEach(
        (mappingClass, mapping) -> {
          if (mappingClass.isAssignableFrom(clazz)) {
            candidates.add(mapping);
          }
        });

    Optional<ExceptionMapping> result =
        candidates.stream()
            .min(Comparator.comparingInt(m -> distance(clazz, m.getExceptionClass())));

    result.ifPresent(mapping -> cache.put(clazz, mapping));

    return result;
  }

  /**
   * Calculates the inheritance distance between two classes in the class hierarchy.
   *
   * <p>The distance is defined as the number of superclass steps from {@code thrown} up to {@code
   * mappingKey}. If {@code mappingKey} is not assignable from {@code thrown}, {@link
   * Integer#MAX_VALUE} is returned to indicate that the mapping is not applicable.
   *
   * @param thrown the actual exception class that was thrown
   * @param mappingKey the exception class associated with a mapping
   * @return the number of inheritance steps from {@code thrown} to {@code mappingKey}, or {@link
   *     Integer#MAX_VALUE} if {@code mappingKey} is not assignable from {@code thrown}
   */
  private int distance(Class<?> thrown, Class<?> mappingKey) {
    if (!mappingKey.isAssignableFrom(thrown)) {
      return Integer.MAX_VALUE;
    }
    int distance = 0;
    Class<?> current = thrown;
    while (current != null && !current.equals(mappingKey)) {
      current = current.getSuperclass();
      distance++;
    }
    return distance;
  }
}
