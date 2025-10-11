package io.github.malczuuu.problem4j.spring.web.processor;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import org.springframework.util.StringUtils;

/**
 * {@link ProblemPostProcessor} implementation that overrides selected fields of a {@link Problem}
 * based on configurable templates.
 *
 * <p>This processor allows the {@code type} and {@code instance} fields to be replaced with custom
 * values defined through {@link PostProcessorSettings}. Each override can include placeholders that
 * are resolved at runtime using data from the current {@link Problem} or {@link ProblemContext}.
 *
 * <p>Supported placeholders:
 *
 * <ul>
 *   <li><b>For {@code type} override:</b>
 *       <ul>
 *         <li>{@code {problem.type}} — replaced with the original problem’s {@code type}
 *       </ul>
 *   <li><b>For {@code instance} override:</b>
 *       <ul>
 *         <li>{@code {problem.instance}} — replaced with the original problem’s {@code instance}
 *         <li>{@code {context.traceId}} — replaced with the current request’s trace identifier, if
 *             available
 *       </ul>
 * </ul>
 *
 * <p>If an override template produces the same value as the existing field, no change is applied.
 * If neither override results in a change, the original {@link Problem} instance is returned
 * unchanged.
 *
 * <p>Example configuration:
 *
 * <pre>{@code
 * problem4j.type-override=https://errors.example.com/{problem.type}
 * problem4j.instance-override=/errors/{context.traceId}
 * }</pre>
 */
public class OverridingProblemPostProcessor implements ProblemPostProcessor {

  private final PostProcessorSettings settings;

  public OverridingProblemPostProcessor(PostProcessorSettings settings) {
    this.settings = settings;
  }

  /**
   * Applies the configured field overrides to the given {@link Problem}, if applicable.
   *
   * <p>This method substitutes placeholders in the configured templates with runtime values and
   * builds a new {@link Problem} only if the resulting field values differ from the originals.
   *
   * @param context the {@link ProblemContext} providing request-scoped metadata such as a trace
   *     identifier
   * @param problem the original {@link Problem} to process
   * @return a new {@link Problem} with overridden fields, or the original instance if no
   *     modifications were applied
   */
  @Override
  public Problem process(ProblemContext context, Problem problem) {
    if (context == null) {
      context = ProblemContext.empty();
    }

    ProblemBuilder builder = null;

    if (StringUtils.hasLength(settings.getTypeOverride())) {
      String newType = overrideType(problem);
      if (newType != null && !newType.equals(stringOrEmpty(problem.getType()))) {
        builder = problem.toBuilder().type(newType);
      }
    }

    if (StringUtils.hasLength(settings.getInstanceOverride())) {
      String newInstance = overrideInstance(problem, context);
      if (newInstance != null && !newInstance.equals(stringOrEmpty(problem.getInstance()))) {
        if (builder == null) {
          builder = problem.toBuilder();
        }
        builder.instance(newInstance);
      }
    }

    return builder != null ? builder.build() : problem;
  }

  private String overrideType(Problem problem) {
    String override = settings.getTypeOverride();
    if (!StringUtils.hasLength(override)) {
      return stringOrEmpty(problem.getType());
    }

    String problemType = isTypeUnset(problem) ? stringOrEmpty(problem.getType()) : "";
    override = override.replace("{problem.type}", problemType != null ? problemType : "");
    return override;
  }

  private boolean isTypeUnset(Problem problem) {
    return !Problem.BLANK_TYPE.equals(problem.getType());
  }

  private String overrideInstance(Problem problem, ProblemContext context) {
    String override = settings.getInstanceOverride();
    if (!StringUtils.hasLength(override)) {
      return stringOrEmpty(problem.getInstance());
    }

    if (StringUtils.hasLength(context.getTraceId())) {
      override = override.replace("{context.traceId}", context.getTraceId());
    } else {
      override = override.replace("{context.traceId}", "");
    }

    String problemInstance = stringOrEmpty(problem.getInstance());
    if (StringUtils.hasLength(problemInstance)) {
      override = override.replace("{problem.instance}", problemInstance);
    } else {
      override = override.replace("{problem.instance}", "");
    }

    return override;
  }

  private String stringOrEmpty(Object value) {
    return value != null ? value.toString() : "";
  }
}
