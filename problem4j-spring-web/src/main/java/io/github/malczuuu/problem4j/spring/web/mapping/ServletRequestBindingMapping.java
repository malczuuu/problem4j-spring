package io.github.malczuuu.problem4j.spring.web.mapping;

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
import io.github.malczuuu.problem4j.spring.web.format.DetailFormat;
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
 * {@code ServletRequestBindingMapping} is an {@link
 * io.github.malczuuu.problem4j.spring.web.mapping.ExceptionMapping} implementation that maps {@link
 * ServletRequestBindingException} and its common subtypes to {@link Problem} representations.
 *
 * <p>Each supported exception type is mapped to a {@code Problem} with {@code 400 Bad Request}
 * status and a human-readable detail message determined by the configured {@link DetailFormat}.
 * Additional metadata about the missing element (such as parameter name, header name, or attribute
 * name) is added as extensions.
 *
 * @see ServletRequestBindingException
 * @see MissingPathVariableException
 * @see MissingServletRequestParameterException
 * @see MissingRequestHeaderException
 * @see MissingRequestCookieException
 * @see Problem
 * @see ProblemStatus#BAD_REQUEST
 */
public class ServletRequestBindingMapping implements ExceptionMapping {

  private static final Pattern MISSING_ATTRIBUTE_PATTERN =
      Pattern.compile("^Missing (session|request) attribute '([^']+)'");

  private final DetailFormat detailFormat;

  /**
   * Creates a new {@code ServletRequestBindingMapping} with the given {@link DetailFormat}
   * strategy.
   *
   * @param detailFormat the format strategy for problem detail messages; must not be {@code null}
   */
  public ServletRequestBindingMapping(DetailFormat detailFormat) {
    this.detailFormat = detailFormat;
  }

  /**
   * Returns the exception class handled by this mapping.
   *
   * @return the exception type ({@link ServletRequestBindingException}) that this mapping supports
   */
  @Override
  public Class<? extends Exception> getExceptionClass() {
    return ServletRequestBindingException.class;
  }

  @Override
  public Problem map(Exception ex, HttpHeaders headers, HttpStatusCode status) {
    ProblemBuilder builder = Problem.builder().status(ProblemStatus.BAD_REQUEST);

    if (ex instanceof MissingPathVariableException e) {
      builder =
          builder
              .detail(detailFormat.format(MISSING_PATH_VARIABLE_DETAIL))
              .extension(NAME_EXTENSION, e.getVariableName());
    } else if (ex instanceof MissingServletRequestParameterException e) {
      builder =
          builder
              .detail(detailFormat.format(MISSING_REQUEST_PARAM_DETAIL))
              .extension(PARAM_EXTENSION, e.getParameterName())
              .extension(KIND_EXTENSION, e.getParameterType().toLowerCase(Locale.ROOT));
    } else if (ex instanceof MissingRequestHeaderException e) {
      builder =
          builder
              .detail(detailFormat.format(MISSING_HEADER_DETAIL))
              .extension(HEADER_EXTENSION, e.getHeaderName());
    } else if (ex instanceof MissingRequestCookieException e) {
      builder =
          builder
              .detail(detailFormat.format(MISSING_COOKIE_DETAIL))
              .extension(COOKIE_EXTENSION, e.getCookieName());
    } else if (ex instanceof ServletRequestBindingException e) {
      Matcher matcher = MISSING_ATTRIBUTE_PATTERN.matcher(e.getMessage());
      if (matcher.find()) {
        String scope = matcher.group(1);
        String attribute = matcher.group(2);
        builder = extentAttributeDetail(scope, builder, attribute);
      }
    }

    return builder.build();
  }

  private ProblemBuilder extentAttributeDetail(
      String scope, ProblemBuilder builder, String attribute) {
    if (scope.equals("session")) {
      builder = builder.detail(detailFormat.format(MISSING_SESSION_ATTRIBUTE_DETAIL));
    } else {
      builder = builder.detail(detailFormat.format(MISSING_REQUEST_ATTRIBUTE_DETAIL));
    }
    return builder.extension("attribute", attribute);
  }
}
