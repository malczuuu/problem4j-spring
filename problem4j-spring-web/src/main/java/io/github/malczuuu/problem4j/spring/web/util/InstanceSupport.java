package io.github.malczuuu.problem4j.spring.web.util;

import io.github.malczuuu.problem4j.spring.web.ProblemContext;
import org.jetbrains.annotations.ApiStatus;
import org.springframework.util.StringUtils;

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
public final class InstanceSupport {

  @ApiStatus.Internal
  public static String overrideInstance(String instanceOverride, ProblemContext context) {
    if (!StringUtils.hasLength(instanceOverride)) {
      return null;
    }
    if (context == null || !StringUtils.hasLength(context.getTraceId())) {
      return null;
    }
    return instanceOverride.replace("{traceId}", context.getTraceId());
  }

  private InstanceSupport() {}
}
