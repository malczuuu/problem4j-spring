package io.github.malczuuu.problem4j.spring.web.internal;

import java.util.UUID;
import org.jetbrains.annotations.ApiStatus;

/**
 * <b>For internal use only.</b>
 *
 * <p>This class is intended for internal use within the {@code problem4j-spring-*} libraries and
 * should not be used directly by external applications. The API may change or be removed without
 * notice.
 *
 * <p><b>Use at your own risk</b>
 *
 * @implNote This is an internal API and may change at any time.
 * @see ApiStatus.Internal
 */
@ApiStatus.Internal
public final class TracingSupport {

  public static final String TRACE_ID_ATTR = "attr.tracing.traceId";
  public static final String INSTANCE_OVERRIDE_ATTR = "attr.tracing.instanceOverride";

  public static String getRandomTraceId() {
    return "urn:uuid:" + UUID.randomUUID();
  }

  private TracingSupport() {}
}
