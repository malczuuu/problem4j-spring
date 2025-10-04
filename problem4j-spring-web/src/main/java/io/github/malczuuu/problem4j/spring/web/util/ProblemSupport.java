package io.github.malczuuu.problem4j.spring.web.util;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;

public final class ProblemSupport {

  public static final String MISSING_REQUEST_PARAM_DETAIL = "Missing request param";
  public static final String MISSING_REQUEST_PART_DETAIL = "Missing request part";
  public static final String MISSING_HEADER_DETAIL = "Missing header";
  public static final String MISSING_PATH_VARIABLE_DETAIL = "Missing path variable";
  public static final String MISSING_COOKIE_DETAIL = "Missing cookie";
  public static final String MISSING_REQUEST_ATTRIBUTE_DETAIL = "Missing request attribute";
  public static final String MISSING_SESSION_ATTRIBUTE_DETAIL = "Missing session attribute";

  public static final String QUERY_PARAMETER_LABEL = "query parameter";
  public static final String REQUEST_PART_LABEL = "request part";
  public static final String HEADER_LABEL = "header";
  public static final String PATH_PARAMETER_LABEL = "path parameter";
  public static final String COOKIE_LABEL = "cookie";
  public static final String REQUEST_ATTRIBUTE_LABEL = "request attribute";
  public static final String SESSION_ATTRIBUTE_LABEL = "session attribute";

  public static final String PARAM_EXTENSION = "param";
  public static final String KIND_EXTENSION = "kind";
  public static final String HEADER_EXTENSION = "header";
  public static final String ATTRIBUTE_EXTENSION = "attribute";
  public static final String NAME_EXTENSION = "name";
  public static final String COOKIE_EXTENSION = "cookie";

  /**
   * Default {@code 500 Internal Server Error} to be thrown in overwritten exception handling. Any
   * extensive detail of the error are specifically hidden so technology details won't be leaked.
   * {@link Problem} objects are immutable and thread-safe so they can be reused.
   */
  public static final Problem INTERNAL_SERVER_ERROR =
      Problem.builder().status(ProblemStatus.INTERNAL_SERVER_ERROR).build();

  private ProblemSupport() {}
}
