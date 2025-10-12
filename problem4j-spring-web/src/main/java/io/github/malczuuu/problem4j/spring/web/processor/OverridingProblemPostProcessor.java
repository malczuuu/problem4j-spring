package io.github.malczuuu.problem4j.spring.web.processor;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.spring.web.annotation.ProblemMappingProcessor;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import java.util.Optional;
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
 *         <li>{@code {problem.type}} - replaced with the original problem’s {@code type}
 *       </ul>
 *   <li><b>For {@code instance} override:</b>
 *       <ul>
 *         <li>{@code {problem.instance}} - replaced with the original problem’s {@code instance}
 *         <li>{@code {context.traceId}} - replaced with the current request’s trace identifier, if
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
   * Applies configured overrides to {@code type} and/or {@code instance}.
   *
   * <p>About {@code about:blank}: when the original type is {@code null} or {@code
   * Problem#BLANK_TYPE}, the placeholder {@code {problem.type}} resolves to an empty string
   * (templates naturally collapse). If the template is exactly {@code {problem.type}}, the original
   * {@code about:blank} value is preserved instead of becoming empty.
   */
  @Override
  public Problem process(ProblemContext context, Problem problem) {
    if (context == null) {
      context = ProblemContext.empty();
    }

    ProblemBuilder builder = null;

    // Override type only if {problem.type} is referenced and original type is valid
    if (StringUtils.hasLength(settings.getTypeOverride())) {
      String template = settings.getTypeOverride();
      boolean requiresProblemType = template.contains("{problem.type}");
      boolean hasProblemType = isTypeSet(problem);

      boolean canOverride = isCanOverride(requiresProblemType, hasProblemType);

      if (canOverride) {
        Optional<String> newTypeCandidate = overrideType(problem);
        if (newTypeCandidate.isPresent()) {
          builder = problem.toBuilder().type(newTypeCandidate.get());
        }
      }
    }

    if (StringUtils.hasLength(settings.getInstanceOverride())) {
      String template = settings.getInstanceOverride();
      boolean needsProblemInstance = template.contains("{problem.instance}");
      boolean needsTraceId = template.contains("{context.traceId}");
      boolean hasProblemInstance =
          problem.getInstance() != null && !problem.getInstance().toString().isEmpty();
      boolean hasTraceId = StringUtils.hasLength(context.getTraceId());

      boolean canOverride =
          (!needsProblemInstance || hasProblemInstance) && (!needsTraceId || hasTraceId);

      if (canOverride) {
        String newInstance = overrideInstance(problem, context);
        if (!newInstance.equals(stringOrEmpty(problem.getInstance()))) {
          if (builder == null) {
            builder = problem.toBuilder();
          }
          builder.instance(newInstance);
        }
      }
    }

    return builder != null ? builder.build() : problem;
  }

  private static boolean isCanOverride(boolean requiresProblemType, boolean hasProblemType) {
    return !requiresProblemType || hasProblemType;
  }

  /**
   * Because this implementation does not try to resolve dynamic fields, it does not reuse the code
   * from {@code DefaultProblemMappingProcessor}. Instead, we rely on simple substring replacement
   * in form of {@link String#replace(CharSequence, CharSequence)}, and then removing all remaining
   * unknown variables.
   *
   * <p>Note that it does not override {@link Problem#BLANK_TYPE} values.
   *
   * <p>If the algorithm discovers remaining placeholders that are unresolved, overriding is aborted
   * and original value is restored.
   *
   * @see io.github.malczuuu.problem4j.spring.web.annotation.DefaultProblemMappingProcessor
   */
  private Optional<String> overrideType(Problem problem) {
    if (!isTypeSet(problem)) {
      return Optional.empty();
    }

    String template = settings.getTypeOverride();
    String original = stringOrEmpty(problem.getType());
    String resolved = template.replace("{problem.type}", original);

    if (hasRemainingUnknownPlaceholders(resolved)) {
      return Optional.empty();
    }

    return Optional.of(resolved).filter(str -> !str.isEmpty());
  }

  private boolean isTypeSet(Problem problem) {
    return problem.getType() != null
        && !problem.getType().toString().isEmpty()
        && !Problem.BLANK_TYPE.equals(problem.getType());
  }

  /**
   * Because this implementation does not try to resolve dynamic fields, it does not reuse the code
   * from {@code DefaultProblemMappingProcessor}. Instead, we rely on simple substring replacement
   * in form of {@link String#replace(CharSequence, CharSequence)}, and then removing all remaining
   * unknown variables.
   *
   * <p>If the algorithm discovers remaining placeholders that are unresolved, overriding is aborted
   * and original value is restored.
   *
   * @see io.github.malczuuu.problem4j.spring.web.annotation.DefaultProblemMappingProcessor
   */
  private String overrideInstance(Problem problem, ProblemContext context) {
    String template = settings.getInstanceOverride();
    String instanceValue = stringOrEmpty(problem.getInstance());
    String traceIdValue = stringOrEmpty(context.getTraceId());

    template = template.replace("{problem.instance}", instanceValue);
    template = template.replace("{context.traceId}", traceIdValue);

    if (hasRemainingUnknownPlaceholders(template)) {
      return problem.getInstance().toString();
    }

    return template;
  }

  private String stringOrEmpty(Object value) {
    return value != null ? value.toString() : "";
  }

  /**
   * Because this implementation does not try to resolve dynamic fields, it does not reuse the code
   * from {@code DefaultProblemMappingProcessor}. Instead, we rely on simple substring replacement
   * in form of {@link String#replace(CharSequence, CharSequence)}, and then removing all remaining
   * unknown variables.
   *
   * @see io.github.malczuuu.problem4j.spring.web.annotation.DefaultProblemMappingProcessor
   */
  private boolean hasRemainingUnknownPlaceholders(String value) {
    return ProblemMappingProcessor.PLACEHOLDER.matcher(value).find();
  }
}
