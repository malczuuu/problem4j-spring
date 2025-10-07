package io.github.malczuuu.problem4j.spring.web.internal;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import org.junit.jupiter.api.Test;

class InstanceSupportTest {

  @Test
  void givenNullInstanceOverride_Instance_shouldReturnNull() {
    String result = InstanceSupport.overrideInstance(null, () -> "abc");

    assertThat(result).isNull();
  }

  @Test
  void givenEmptyInstanceOverride_Instance_shouldReturnNull() {
    String result = InstanceSupport.overrideInstance("", () -> "abc");

    assertThat(result).isNull();
  }

  @Test
  void givenNoTraceIdInContext_shouldReturnNull() {
    ProblemContext context = () -> null;

    String result =
        InstanceSupport.overrideInstance("https://example.org/instances/{traceId}", context);

    assertThat(result).isNull();
  }

  @Test
  void givenNullContext_shouldReturnNull() {
    String input = "https://example.org/instances/{traceId}";

    String result = InstanceSupport.overrideInstance(input, null);

    assertThat(result).isNull();
  }

  @Test
  void givenPlaceholderPresent_shouldReplaceWithTraceId() {
    ProblemContext context = () -> "12345";

    String result =
        InstanceSupport.overrideInstance("https://example.org/instances/{traceId}", context);

    assertThat(result).isEqualTo("https://example.org/instances/12345");
  }

  @Test
  void givenMultiplePlaceholders_shouldReplaceAllOccurrences() {
    ProblemContext context = () -> "xyz";

    String result = InstanceSupport.overrideInstance("{traceId}-{traceId}", context);

    assertThat(result).isEqualTo("xyz-xyz");
  }

  @Test
  void givenNoPlaceholder_shouldReturnOriginal() {
    ProblemContext context = () -> "12345";

    String result = InstanceSupport.overrideInstance("https://example.org/instances/abc", context);

    assertThat(result).isEqualTo("https://example.org/instances/abc");
  }
}
