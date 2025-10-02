package io.github.malczuuu.problem4j.spring.web;

import io.github.malczuuu.problem4j.spring.web.mapping.ExceptionMapping;
import java.util.Optional;

/**
 * Registry for managing per-exception {@link ExceptionMapping} instances.
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
 */
public interface ExceptionMappingStore {

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
  Optional<ExceptionMapping> findMapping(Class<? extends Exception> clazz);
}
