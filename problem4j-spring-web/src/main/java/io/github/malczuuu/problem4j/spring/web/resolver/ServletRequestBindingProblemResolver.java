package io.github.malczuuu.problem4j.spring.web.resolver;

import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.ATTRIBUTE_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.COOKIE_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.HEADER_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.KIND_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.MISSING_COOKIE_DETAIL;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.MISSING_HEADER_DETAIL;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.MISSING_PATH_VARIABLE_DETAIL;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.MISSING_REQUEST_ATTRIBUTE_DETAIL;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.MISSING_REQUEST_PARAM_DETAIL;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.MISSING_SESSION_ATTRIBUTE_DETAIL;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.NAME_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.PARAM_EXTENSION;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.format.ProblemFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;

/**
 * An {@link ProblemResolver} implementation that maps {@link ServletRequestBindingException} and
 * its common subtypes to {@link Problem} representations.
 *
 * <p>Each supported exception type is mapped to a {@code Problem} with {@code 400 Bad Request}
 * status and a human-readable detail message. Additional metadata about the missing element (such
 * as parameter name, header name, or attribute name) is added as extensions.
 *
 * @see ServletRequestBindingException
 * @see MissingPathVariableException
 * @see MissingServletRequestParameterException
 * @see MissingRequestHeaderException
 * @see MissingRequestCookieException
 * @see Problem
 * @see ProblemStatus#BAD_REQUEST
 */
public class ServletRequestBindingProblemResolver extends AbstractProblemResolver {

  private static final Pattern MISSING_ATTRIBUTE_PATTERN =
      Pattern.compile("^Missing (session|request) attribute '([^']+)'");

  public ServletRequestBindingProblemResolver(ProblemFormat problemFormat) {
    super(ServletRequestBindingException.class, problemFormat);
  }

  /**
   * Resolves a {@link ServletRequestBindingException} (or one of its common subclasses) into a
   * {@link ProblemBuilder} with {@link ProblemStatus#BAD_REQUEST} status and appropriate detail
   * plus metadata extensions.
   *
   * <p>Subtype handling:
   *
   * <ul>
   *   <li>{@link MissingPathVariableException}: detail {@code MISSING_PATH_VARIABLE_DETAIL},
   *       extension {@code name}
   *   <li>{@link MissingServletRequestParameterException}: detail {@code
   *       MISSING_REQUEST_PARAM_DETAIL}, extensions {@code param}, {@code kind}
   *   <li>{@link MissingRequestHeaderException}: detail {@code MISSING_HEADER_DETAIL}, extension
   *       {@code header}
   *   <li>{@link MissingRequestCookieException}: detail {@code MISSING_COOKIE_DETAIL}, extension
   *       {@code cookie}
   *   <li>Generic {@link ServletRequestBindingException} whose message matches {@code ^Missing
   *       (session|request) attribute '...'}: detail set to corresponding missing attribute message
   *       and extension {@code attribute}
   * </ul>
   *
   * @param context problem context (unused)
   * @param ex binding-related exception to map
   * @param headers HTTP headers (unused)
   * @param status suggested status (ignored; always BAD_REQUEST)
   * @return builder populated with status, detail, and extensions
   */
  @Override
  public ProblemBuilder resolveBuilder(
      ProblemContext context, Exception ex, HttpHeaders headers, HttpStatusCode status) {
    ProblemBuilder builder = Problem.builder().status(ProblemStatus.BAD_REQUEST);

    if (ex instanceof MissingPathVariableException e) {
      builder =
          builder
              .detail(formatDetail(MISSING_PATH_VARIABLE_DETAIL))
              .extension(NAME_EXTENSION, e.getVariableName());
    } else if (ex instanceof MissingServletRequestParameterException e) {
      builder =
          builder
              .detail(formatDetail(MISSING_REQUEST_PARAM_DETAIL))
              .extension(PARAM_EXTENSION, e.getParameterName())
              .extension(KIND_EXTENSION, e.getParameterType().toLowerCase(Locale.ROOT));
    } else if (ex instanceof MissingRequestHeaderException e) {
      builder =
          builder
              .detail(formatDetail(MISSING_HEADER_DETAIL))
              .extension(HEADER_EXTENSION, e.getHeaderName());
    } else if (ex instanceof MissingRequestCookieException e) {
      builder =
          builder
              .detail(formatDetail(MISSING_COOKIE_DETAIL))
              .extension(COOKIE_EXTENSION, e.getCookieName());
    } else if (ex instanceof ServletRequestBindingException e) {
      Matcher matcher = MISSING_ATTRIBUTE_PATTERN.matcher(e.getMessage());
      if (matcher.find()) {
        String scope = matcher.group(1);
        String attribute = matcher.group(2);
        builder = extentAttributeDetail(scope, builder, attribute);
      }
    }

    return builder;
  }

  /**
   * Applies the appropriate missing attribute detail (session vs request) and adds the attribute
   * name extension.
   *
   * @param scope either "session" or "request" (already validated by regex match)
   * @param builder current problem builder
   * @param attribute missing attribute name extracted from exception message
   * @return updated builder with detail and attribute extension
   */
  private ProblemBuilder extentAttributeDetail(
      String scope, ProblemBuilder builder, String attribute) {
    if (scope.equals("session")) {
      builder = builder.detail(formatDetail(MISSING_SESSION_ATTRIBUTE_DETAIL));
    } else {
      builder = builder.detail(formatDetail(MISSING_REQUEST_ATTRIBUTE_DETAIL));
    }
    return builder.extension(ATTRIBUTE_EXTENSION, attribute);
  }
}
