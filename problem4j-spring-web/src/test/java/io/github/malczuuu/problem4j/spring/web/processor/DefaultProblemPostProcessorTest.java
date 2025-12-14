package io.github.malczuuu.problem4j.spring.web.processor;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.util.StringUtils;

class DefaultProblemPostProcessorTest {

  @Test
  void shouldReturnSameProblemWhenNoOverrides() {
    PostProcessorSettings settings = getSettings(null, null);

    ProblemContext context = ProblemContext.ofTraceId("trace-123");
    Problem problem = Problem.builder().type("bad_request").instance("instance-1").build();

    DefaultProblemPostProcessor processor = new DefaultProblemPostProcessor(settings);
    Problem result = processor.process(context, problem);

    assertThat(result).isSameAs(problem);
  }

  @Test
  void givenEmptyTypeOverride_shouldNotChangeProblemType() {
    PostProcessorSettings settings = getSettings("", null);

    Problem problem = Problem.builder().type("bad_request").instance("instance-1").build();
    DefaultProblemPostProcessor processor = new DefaultProblemPostProcessor(settings);

    Problem result = processor.process(null, problem);

    assertThat(result).isSameAs(problem);
  }

  @Test
  void givenEmptyInstanceOverride_shouldNotChangeProblemInstance() {
    PostProcessorSettings settings = getSettings(null, "");

    Problem problem = Problem.builder().type("bad_request").instance("instance-1").build();
    DefaultProblemPostProcessor processor = new DefaultProblemPostProcessor(settings);

    Problem result = processor.process(null, problem);

    assertThat(result).isSameAs(problem);
  }

  @Test
  void givenStaticTypeOverride_shouldOverrideType() {
    PostProcessorSettings settings = getSettings("/errors/static", null);

    Problem problem = Problem.builder().type("bad_request").instance("instance-1").build();

    DefaultProblemPostProcessor processor = new DefaultProblemPostProcessor(settings);
    Problem result = processor.process(null, problem);

    assertThat(result).isEqualTo(problem.toBuilder().type("/errors/static").build());
  }

  @Test
  void givenStaticInstanceOverride_shouldOverrideInstance() {
    PostProcessorSettings settings = getSettings(null, "/instances/static");

    Problem problem = Problem.builder().type("bad_request").instance("instance-1").build();

    DefaultProblemPostProcessor processor = new DefaultProblemPostProcessor(settings);
    Problem result = processor.process(null, problem);

    assertThat(result).isEqualTo(problem.toBuilder().instance("/instances/static").build());
  }

  @Test
  void givenProblemTypePlaceholderWithTypeValue_shouldOverrideType() {
    PostProcessorSettings settings = getSettings("/errors/{problem.type}", null);

    Problem problem = Problem.builder().type("bad_request").instance("instance-1").build();

    DefaultProblemPostProcessor processor = new DefaultProblemPostProcessor(settings);
    Problem result = processor.process(null, problem);

    assertThat(result).isEqualTo(problem.toBuilder().type("/errors/bad_request").build());
  }

  @Test
  void givenProblemTypePlaceholderWithEmptyTypeValue_shouldNotOverrideType() {
    PostProcessorSettings settings = getSettings("/errors/{problem.type}", null);

    Problem problem = Problem.builder().type("").instance("instance-1").build();

    DefaultProblemPostProcessor processor = new DefaultProblemPostProcessor(settings);
    Problem result = processor.process(null, problem);

    assertThat(result).isSameAs(problem);
  }

  @Test
  void givenContextTraceIdPlaceholderWithTraceIdValue_shouldOverrideInstance() {
    PostProcessorSettings settings = getSettings(null, "trace:{context.traceId}");

    ProblemContext context = ProblemContext.ofTraceId("trace-abc");
    Problem problem = Problem.builder().type("bad_request").instance("instance-1").build();

    DefaultProblemPostProcessor processor = new DefaultProblemPostProcessor(settings);
    Problem result = processor.process(context, problem);

    assertThat(result).isEqualTo(problem.toBuilder().instance("trace:trace-abc").build());
  }

  @Test
  void givenContextTraceIdPlaceholderWithoutTraceIdValue_shouldNotOverrideInstance() {
    PostProcessorSettings settings = getSettings(null, "trace:{context.traceId}");

    ProblemContext context = ProblemContext.empty();
    Problem problem = Problem.builder().type("bad_request").instance("instance-1").build();

    DefaultProblemPostProcessor processor = new DefaultProblemPostProcessor(settings);
    Problem result = processor.process(context, problem);

    assertThat(result).isSameAs(problem);
  }

  @Test
  void givenContextTraceIdPlaceholderWithEmptyTraceIdValue_shouldNotOverrideInstance() {
    PostProcessorSettings settings = getSettings(null, "trace:{context.traceId}");

    ProblemContext context = ProblemContext.ofTraceId("");
    Problem problem = Problem.builder().type("bad_request").instance("instance-1").build();

    DefaultProblemPostProcessor processor = new DefaultProblemPostProcessor(settings);
    Problem result = processor.process(context, problem);

    assertThat(result).isSameAs(problem);
  }

  @Test
  void givenProblemInstancePlaceholderWithValue_shouldOverrideInstance() {
    PostProcessorSettings settings = getSettings(null, "original:{problem.instance}");

    Problem problem = Problem.builder().type("bad_request").instance("inst-777").build();

    DefaultProblemPostProcessor processor = new DefaultProblemPostProcessor(settings);
    Problem result = processor.process(null, problem);

    assertThat(result).isEqualTo(problem.toBuilder().instance("original:inst-777").build());
  }

  @Test
  void givenProblemInstancePlaceholderWithoutValue_shouldNotOverrideInstance() {
    PostProcessorSettings settings = getSettings(null, "original:{problem.instance}");

    Problem problem = Problem.builder().type("bad_request").build();

    DefaultProblemPostProcessor processor = new DefaultProblemPostProcessor(settings);
    Problem result = processor.process(null, problem);

    assertThat(result).isSameAs(problem);
  }

  @Test
  void givenProblemInstancePlaceholderWithEmptyValue_shouldNotOverrideInstance() {
    PostProcessorSettings settings = getSettings(null, "original:{problem.instance}");

    Problem problem = Problem.builder().type("bad_request").instance("").build();

    DefaultProblemPostProcessor processor = new DefaultProblemPostProcessor(settings);
    Problem result = processor.process(null, problem);

    assertThat(result).isSameAs(problem);
  }

  @ParameterizedTest(name = "{index} => problemInstance={0}, contextTraceId={1}, expected={2}")
  @CsvSource(
      value = {
        "'inst', 'trace-X', 'trace-X/inst'",
        "'inst', '',        ''",
        "'inst', null,      ''",
        "'',     'trace-Y', ''",
        "null,   'trace-Y', ''"
      },
      nullValues = "null")
  void givenProblemInstanceAndContextTraceIdPlaceholders_shouldNotOverrideIfAnyIsMissing(
      String problemInstance, String contextTraceId, String expectedResult) {
    PostProcessorSettings settings = getSettings(null, "{context.traceId}/{problem.instance}");

    ProblemBuilder builder = Problem.builder().type("bad_request");
    if (problemInstance != null) {
      builder = builder.instance(problemInstance);
    }
    Problem problem = builder.build();

    ProblemContext context = ProblemContext.ofTraceId(contextTraceId);

    DefaultProblemPostProcessor processor = new DefaultProblemPostProcessor(settings);
    Problem result = processor.process(context, problem);

    if (StringUtils.hasLength(expectedResult)) {
      assertThat(result).isEqualTo(problem.toBuilder().instance(expectedResult).build());
    } else {
      assertThat(result).isSameAs(problem);
    }
  }

  @Test
  void givenOverrideWithUnknownPlaceholders_shouldRemoveUnknownPlaceholders() {
    PostProcessorSettings settings = getSettings("type-{unknown}", "instance-{unknown}");
    DefaultProblemPostProcessor processor = new DefaultProblemPostProcessor(settings);

    Problem problem = Problem.builder().type("bad_request").instance("instance-1").build();
    Problem result = processor.process(ProblemContext.ofTraceId("x"), problem);

    assertThat(result).isSameAs(problem);
  }

  private PostProcessorSettings getSettings(String typeOverride, String instanceOverride) {
    return new PostProcessorSettings() {
      @Override
      public String getTypeOverride() {
        return typeOverride;
      }

      @Override
      public String getInstanceOverride() {
        return instanceOverride;
      }
    };
  }
}
