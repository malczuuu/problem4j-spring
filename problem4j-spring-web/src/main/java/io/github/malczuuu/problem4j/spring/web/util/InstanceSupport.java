package io.github.malczuuu.problem4j.spring.web.util;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.spring.web.ProblemContext;
import org.springframework.util.StringUtils;

public final class InstanceSupport {

  public static Problem overrideInstance(
      Problem problem, String instanceOverride, ProblemContext context) {
    String instance = overrideInstance(instanceOverride, context);
    if (instance != null) {
      return problem.toBuilder().instance(instance).build();
    }
    return problem;
  }

  public static ProblemBuilder overrideInstance(
      ProblemBuilder builder, String instanceOverride, ProblemContext context) {
    String instance = overrideInstance(instanceOverride, context);
    if (instance != null) {
      builder = builder.instance(instance);
    }
    return builder;
  }

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
