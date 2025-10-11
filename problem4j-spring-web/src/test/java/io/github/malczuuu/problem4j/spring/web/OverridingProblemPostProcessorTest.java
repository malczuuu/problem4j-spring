package io.github.malczuuu.problem4j.spring.web;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.processor.OverridingProblemPostProcessor;
import io.github.malczuuu.problem4j.spring.web.processor.PostProcessorSettings;
import org.junit.jupiter.api.Test;

class OverridingProblemPostProcessorTest {

  @Test
  void shouldReturnSameProblemWhenNoOverrides() {
    PostProcessorSettings settings = new ProblemProperties(null, null, null, null, null);
    OverridingProblemPostProcessor processor = new OverridingProblemPostProcessor(settings);

    Problem problem = problem("bad_request", "instance-1");
    ProblemContext context = ProblemContext.ofTraceId("trace-123");

    Problem result = processor.process(context, problem);

    assertThat(result).isSameAs(problem);
  }

  @Test
  void shouldOverrideTypeWithStaticValue() {
    PostProcessorSettings settings =
        new ProblemProperties(null, null, "/errors/static", null, null);
    OverridingProblemPostProcessor processor = new OverridingProblemPostProcessor(settings);

    Problem problem = problem("bad_request", "instance-1");
    Problem result = processor.process(null, problem);

    assertThat(result.getType().toString()).isEqualTo("/errors/static");
    assertThat(result.getInstance().toString()).isEqualTo("instance-1");
  }

  @Test
  void shouldOverrideTypeUsingPlaceholder() {
    PostProcessorSettings settings =
        new ProblemProperties(null, null, "/errors/{problem.type}", null, null);
    OverridingProblemPostProcessor processor = new OverridingProblemPostProcessor(settings);

    Problem problem = problem("bad_request", "instance-1");
    Problem result = processor.process(null, problem);

    assertThat(result.getType().toString()).isEqualTo("/errors/bad_request");
  }

  @Test
  void shouldOverrideInstanceUsingContextTraceId() {
    PostProcessorSettings settings =
        new ProblemProperties(null, null, null, "trace:{context.traceId}", null);
    OverridingProblemPostProcessor processor = new OverridingProblemPostProcessor(settings);

    Problem problem = problem("bad_request", "instance-1");
    ProblemContext context = ProblemContext.ofTraceId("trace-abc");

    Problem result = processor.process(context, problem);

    assertThat(result.getInstance().toString()).isEqualTo("trace:trace-abc");
  }

  @Test
  void shouldOverrideInstanceUsingProblemInstancePlaceholder() {
    PostProcessorSettings settings =
        new ProblemProperties(null, null, null, "original:{problem.instance}", null);
    OverridingProblemPostProcessor processor = new OverridingProblemPostProcessor(settings);

    Problem problem = problem("bad_request", "inst-777");
    ProblemContext context = ProblemContext.ofTraceId("trace-999");

    Problem result = processor.process(context, problem);

    assertThat(result.getInstance().toString()).isEqualTo("original:inst-777");
  }

  @Test
  void shouldReplaceBothContextAndProblemPlaceholders() {
    PostProcessorSettings settings =
        new ProblemProperties(null, null, null, "{context.traceId}/{problem.instance}", null);
    OverridingProblemPostProcessor processor = new OverridingProblemPostProcessor(settings);

    Problem problem = problem("bad_request", "orig-inst");
    ProblemContext context = ProblemContext.ofTraceId("trace-X");

    Problem result = processor.process(context, problem);

    assertThat(result.getInstance().toString()).isEqualTo("trace-X/orig-inst");
  }

  @Test
  void shouldHandleNullContextGracefully() {
    PostProcessorSettings settings =
        new ProblemProperties(null, null, null, "id_{context.traceId}", null);
    OverridingProblemPostProcessor processor = new OverridingProblemPostProcessor(settings);

    Problem problem = problem("bad_request", "orig");
    Problem result = processor.process(null, problem);

    assertThat(result.getInstance().toString()).isEqualTo("id_");
  }

  @Test
  void shouldHandleMissingProblemFieldsGracefully() {
    PostProcessorSettings settings =
        new ProblemProperties(null, null, "/errors/{problem.type}", "x_{problem.instance}", null);
    OverridingProblemPostProcessor processor = new OverridingProblemPostProcessor(settings);

    Problem problem = Problem.builder().build();
    ProblemContext context = ProblemContext.ofTraceId("t1");

    Problem result = processor.process(context, problem);

    assertThat(result.getType().toString()).isEqualTo("/errors/");
    assertThat(result.getInstance().toString()).isEqualTo("x_");
  }

  @Test
  void shouldNotOverrideIfResultIsSameAsOriginal() {
    PostProcessorSettings settings =
        new ProblemProperties(null, null, "{problem.type}", null, null);
    OverridingProblemPostProcessor processor = new OverridingProblemPostProcessor(settings);

    Problem problem = problem("bad_request", "instance-1");
    Problem result = processor.process(null, problem);

    assertThat(result).isSameAs(problem);
  }

  @Test
  void givenNullInstanceOverride_shouldReturnSameProblem() {
    PostProcessorSettings settings = new ProblemProperties(null, null, null, null, null);
    OverridingProblemPostProcessor processor = new OverridingProblemPostProcessor(settings);

    Problem problem = problem("type-1", "abc");
    ProblemContext context = ProblemContext.ofTraceId("trace-123");

    Problem result = processor.process(context, problem);

    assertThat(result).isSameAs(problem);
  }

  @Test
  void givenEmptyInstanceOverride_shouldReturnSameProblem() {
    PostProcessorSettings settings = new ProblemProperties(null, null, null, "", null);
    OverridingProblemPostProcessor processor = new OverridingProblemPostProcessor(settings);

    Problem problem = problem("type-1", "abc");
    ProblemContext context = ProblemContext.ofTraceId("trace-123");

    Problem result = processor.process(context, problem);

    assertThat(result).isSameAs(problem);
  }

  @Test
  void givenNoTraceIdInContext_shouldNotReplacePlaceholder() {
    PostProcessorSettings settings =
        new ProblemProperties(
            null, null, null, "https://example.org/instances/{context.traceId}", null);
    OverridingProblemPostProcessor processor = new OverridingProblemPostProcessor(settings);

    Problem problem = problem("type-1", "orig");
    ProblemContext context = () -> null;

    Problem result = processor.process(context, problem);

    // When context.traceId is null, placeholder should be replaced with empty string
    assertThat(result.getInstance().toString()).isEqualTo("https://example.org/instances/");
  }

  @Test
  void givenNullContext_shouldReplacePlaceholderWithEmptyString() {
    PostProcessorSettings settings =
        new ProblemProperties(
            null, null, null, "https://example.org/instances/{context.traceId}", null);
    OverridingProblemPostProcessor processor = new OverridingProblemPostProcessor(settings);

    Problem problem = problem("type-1", "orig");

    Problem result = processor.process(null, problem);

    assertThat(result.getInstance().toString()).isEqualTo("https://example.org/instances/");
  }

  @Test
  void givenPlaceholderPresent_shouldReplaceWithTraceId() {
    PostProcessorSettings settings =
        new ProblemProperties(
            null, null, null, "https://example.org/instances/{context.traceId}", null);
    OverridingProblemPostProcessor processor = new OverridingProblemPostProcessor(settings);

    Problem problem = problem("type-1", "orig");
    ProblemContext context = ProblemContext.ofTraceId("12345");

    Problem result = processor.process(context, problem);

    assertThat(result.getInstance().toString()).isEqualTo("https://example.org/instances/12345");
  }

  @Test
  void givenMultiplePlaceholders_shouldReplaceAllOccurrences() {
    PostProcessorSettings settings =
        new ProblemProperties(null, null, null, "{context.traceId}-{context.traceId}", null);
    OverridingProblemPostProcessor processor = new OverridingProblemPostProcessor(settings);

    Problem problem = problem("type-1", "orig");
    ProblemContext context = ProblemContext.ofTraceId("xyz");

    Problem result = processor.process(context, problem);

    assertThat(result.getInstance().toString()).isEqualTo("xyz-xyz");
  }

  @Test
  void givenNoPlaceholder_shouldReturnStaticValue() {
    PostProcessorSettings settings =
        new ProblemProperties(null, null, null, "https://example.org/instances/abc", null);
    OverridingProblemPostProcessor processor = new OverridingProblemPostProcessor(settings);

    Problem problem = problem("type-1", "orig");
    ProblemContext context = ProblemContext.ofTraceId("12345");

    Problem result = processor.process(context, problem);

    assertThat(result.getInstance().toString()).isEqualTo("https://example.org/instances/abc");
  }

  private Problem problem(String type, String instance) {
    return Problem.builder().type(type).instance(instance).build();
  }
}
