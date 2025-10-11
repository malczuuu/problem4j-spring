package io.github.malczuuu.problem4j.spring.web.processor;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.spring.web.annotation.ProblemMappingProcessor;
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

    if (StringUtils.hasLength(settings.getTypeOverride())) {
      String newType = overrideType(problem);
      if (StringUtils.hasLength(newType) && !newType.equals(stringOrEmpty(problem.getType()))) {
        builder = problem.toBuilder().type(newType);
      }
    }

    if (StringUtils.hasLength(settings.getInstanceOverride())) {
      String newInstance = overrideInstance(problem, context);
      if (!newInstance.equals(stringOrEmpty(problem.getInstance()))) {
        if (builder == null) {
          builder = problem.toBuilder();
        }
        builder.instance(newInstance);
      }
    }

    return builder != null ? builder.build() : problem;
  }

  /**
   * Because this implementation does not try to resolve dynamic fields, it does not reuse the code
   * from {@code DefaultProblemMappingProcessor}. Instead, we rely on simple substring replacement
   * in form of {@link String#replace(CharSequence, CharSequence)}, and then removing all remaining
   * unknown variables.
   *
   * @see io.github.malczuuu.problem4j.spring.web.annotation.DefaultProblemMappingProcessor
   */
  private String overrideType(Problem problem) {
    String template = settings.getTypeOverride();
    if (!StringUtils.hasLength(template)) {
      return stringOrEmpty(problem.getType());
    }
    String original = stringOrEmpty(problem.getType());
    String valueForPlaceholder = isTypeSet(problem) ? original : "";
    String resolved = template.replace("{problem.type}", valueForPlaceholder);
    resolved = removeUnknownPlaceholders(resolved);

    if (!isTypeSet(problem)
        && !original.isEmpty()
        && resolved.isEmpty()
        && template.trim().equals("{problem.type}")) {
      return original;
    }

    return resolved;
  }

  private boolean isTypeSet(Problem problem) {
    return problem.getType() != null && !Problem.BLANK_TYPE.equals(problem.getType());
  }

  /**
   * Because this implementation does not try to resolve dynamic fields, it does not reuse the code
   * from {@code DefaultProblemMappingProcessor}. Instead, we rely on simple substring replacement
   * in form of {@link String#replace(CharSequence, CharSequence)}, and then removing all remaining
   * unknown variables.
   *
   * @see io.github.malczuuu.problem4j.spring.web.annotation.DefaultProblemMappingProcessor
   */
  private String overrideInstance(Problem problem, ProblemContext context) {
    String template = settings.getInstanceOverride();
    if (!StringUtils.hasLength(template)) {
      return stringOrEmpty(problem.getInstance());
    }
    template = template.replace("{context.traceId}", stringOrEmpty(context.getTraceId()));
    template = template.replace("{problem.instance}", stringOrEmpty(problem.getInstance()));
    template = removeUnknownPlaceholders(template);
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
  private String removeUnknownPlaceholders(String value) {
    return ProblemMappingProcessor.PLACEHOLDER.matcher(value).replaceAll("");
  }
}
