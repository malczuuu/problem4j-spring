package io.github.malczuuu.problem4j.spring.web;

import io.github.malczuuu.problem4j.spring.web.mapping.ExceptionMapping;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class CachingExceptionMappingStore implements ExceptionMappingStore {

  private final Map<Class<? extends Exception>, ExceptionMapping> mappings;

  private final Map<Class<? extends Exception>, ExceptionMapping> cache = new ConcurrentHashMap<>();

  public CachingExceptionMappingStore(List<ExceptionMapping> exceptionMappings) {
    this.mappings = new HashMap<>(exceptionMappings.size());
    exceptionMappings.forEach(mapping -> mappings.put(mapping.getExceptionClass(), mapping));
  }

  @Override
  public Optional<ExceptionMapping> findMapping(Class<? extends Exception> clazz) {
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
