package io.github.malczuuu.problem4j.spring.web.util;

import java.util.UUID;

public final class TracingSupport {

  public static final String TRACE_ID_ATTR = "attr.tracing.traceId";
  public static final String INSTANCE_OVERRIDE_ATTR = "attr.tracing.instanceOverride";

  public static String getRandomTraceId() {
    return "urn:uuid:" + UUID.randomUUID();
  }

  private TracingSupport() {}
}
