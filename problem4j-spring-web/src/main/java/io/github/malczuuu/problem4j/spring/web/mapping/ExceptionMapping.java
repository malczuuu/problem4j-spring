package io.github.malczuuu.problem4j.spring.web.mapping;

import io.github.malczuuu.problem4j.core.Problem;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;

/**
 * Represents a mapping from a specific Spring-handled {@link Exception} to a {@link Problem}
 * response.
 *
 * <p>Each implementation of this interface handles a single exception type and defines how it
 * should be converted into a {@link Problem} object, including the HTTP headers and status code.
 *
 * <p>Key responsibilities:
 *
 * <ul>
 *   <li>{@link #getExceptionClass()} – returns the exception types this mapping handles.
 *   <li>{@link #map(Exception, HttpHeaders, HttpStatusCode)} – produces the {@link Problem}
 *       response for the given exception.
 * </ul>
 */
public interface ExceptionMapping {

  Class<? extends Exception> getExceptionClass();

  Problem map(Exception ex, HttpHeaders headers, HttpStatusCode status);
}
