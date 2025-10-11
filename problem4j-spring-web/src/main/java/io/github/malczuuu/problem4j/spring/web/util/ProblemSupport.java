package io.github.malczuuu.problem4j.spring.web.util;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

/**
 * Central constants and small utilities used when constructing RFC 7807 {@link Problem} responses.
 *
 * <p>Contains:
 *
 * <ul>
 *   <li>Standard detail message templates (e.g. missing / validation / type mismatch)
 *   <li>Source labels reported by Spring missing-value exceptions
 *   <li>Well-known extension key names added to problems
 *   <li>Status resolution helpers bridging {@link Problem} and Spring HTTP status abstractions
 * </ul>
 *
 * Not intended for instantiation or external mutation.
 */
public final class ProblemSupport {

  // ---------------------------------------------------------------------------
  // Detail message templates (surface-level, human-readable strings) used for
  // Problem.detail across various resolvers. Each corresponds to a specific
  // failure/validation scenario detected by Spring MVC / WebFlux or internal logic.
  // ---------------------------------------------------------------------------
  public static final String MISSING_REQUEST_PARAM_DETAIL = "Missing request param";
  public static final String MISSING_REQUEST_PART_DETAIL = "Missing request part";
  public static final String MISSING_HEADER_DETAIL = "Missing header";
  public static final String MISSING_PATH_VARIABLE_DETAIL = "Missing path variable";
  public static final String MISSING_COOKIE_DETAIL = "Missing cookie";
  public static final String MISSING_REQUEST_ATTRIBUTE_DETAIL = "Missing request attribute";
  public static final String MISSING_SESSION_ATTRIBUTE_DETAIL = "Missing session attribute";
  public static final String TYPE_MISMATCH_DETAIL = "Type mismatch";
  public static final String VALIDATION_FAILED_DETAIL = "Validation failed";
  public static final String MAX_UPLOAD_SIZE_EXCEEDED_DETAIL = "Max upload size exceeded";

  // ---------------------------------------------------------------------------
  // Source labels emitted by Spring (e.g. MissingRequestValueException#getLabel()) or used to
  // classify missing-value scenarios. Resolvers map these labels to the above detail templates
  // and complementary metadata extensions.
  // ---------------------------------------------------------------------------
  public static final String QUERY_PARAMETER_LABEL = "query parameter";
  public static final String REQUEST_PART_LABEL = "request part";
  public static final String HEADER_LABEL = "header";
  public static final String PATH_PARAMETER_LABEL = "path parameter";
  public static final String COOKIE_LABEL = "cookie";
  public static final String REQUEST_ATTRIBUTE_LABEL = "request attribute";
  public static final String SESSION_ATTRIBUTE_LABEL = "session attribute";

  // ---------------------------------------------------------------------------
  // Extension keys injected into Problem.extension(...) to expose structured metadata to clients.
  // Naming is intentionally short & stable for JSON footprint and public contract clarity.
  // ---------------------------------------------------------------------------
  public static final String PARAM_EXTENSION = "param";
  public static final String KIND_EXTENSION = "kind";
  public static final String HEADER_EXTENSION = "header";
  public static final String ATTRIBUTE_EXTENSION = "attribute";
  public static final String NAME_EXTENSION = "name";
  public static final String COOKIE_EXTENSION = "cookie";
  public static final String PROPERTY_EXTENSION = "property";
  public static final String ERRORS_EXTENSION = "errors";
  public static final String MAX_EXTENSION = "max";

  // ---------------------------------------------------------------------------
  // Generic fragments reused inside violations or fallback messages.
  // ---------------------------------------------------------------------------
  public static final String IS_NOT_VALID_ERROR = "is not valid";

  /**
   * Resolves a {@link Problem} to a corresponding {@link HttpStatus}.
   *
   * <p>If the problem's status value is not a valid HTTP status code, {@link
   * HttpStatus#INTERNAL_SERVER_ERROR} is returned as a fallback.
   *
   * @param problem the {@link Problem} instance containing the status code
   * @return the corresponding {@link HttpStatus}, or {@link HttpStatus#INTERNAL_SERVER_ERROR} if
   *     the status is invalid
   */
  public static HttpStatus resolveStatus(Problem problem) {
    try {
      return HttpStatus.valueOf(problem.getStatus());
    } catch (IllegalArgumentException e) {
      return HttpStatus.INTERNAL_SERVER_ERROR;
    }
  }

  /**
   * Resolves an {@link HttpStatusCode} to a corresponding {@link ProblemStatus}.
   *
   * <p>If the status code is {@code null} or not recognized, {@code INTERNAL_SERVER_ERROR} is
   * returned as a fallback.
   *
   * @param status the {@link HttpStatusCode} to resolve
   * @return the corresponding {@link ProblemStatus}, or {@link ProblemStatus#INTERNAL_SERVER_ERROR}
   *     if the status is {@code null} or invalid
   */
  public static ProblemStatus resolveStatus(HttpStatusCode status) {
    return status == null
        ? ProblemStatus.INTERNAL_SERVER_ERROR
        : ProblemStatus.findValue(status.value()).orElse(ProblemStatus.INTERNAL_SERVER_ERROR);
  }

  private ProblemSupport() {}
}
