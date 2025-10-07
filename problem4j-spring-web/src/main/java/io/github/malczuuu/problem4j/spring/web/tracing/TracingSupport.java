package io.github.malczuuu.problem4j.spring.web.tracing;

import java.util.UUID;

/**
 * Utility class providing constants and helper methods for tracing support within the Problem4J.
 */
public final class TracingSupport {

  /** Request attribute key used to store a trace identifier. */
  public static final String TRACE_ID_ATTR = "io.github.malczuuu.problem4j.spring.web.traceId";

  /**
   * Request attribute key used to store override for {@code "instance"} field in {@code Problem}
   * response.
   */
  public static final String INSTANCE_OVERRIDE_ATTR =
      "io.github.malczuuu.problem4j.spring.web.instanceOverride";

  /**
   * Generates a random trace identifier in {@code urn:uuid:<uuid>} format.
   *
   * @return generated trace identifier
   */
  public static String getRandomTraceId() {
    return "urn:uuid:" + UUID.randomUUID();
  }

  private TracingSupport() {}
}
