package io.github.malczuuu.problem4j.spring.web.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.spring.web.util.StaticProblemContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SimpleProblemMappingProcessorTest {

  private SimpleProblemMappingProcessor processor;

  @BeforeEach
  void beforeEach() {
    processor = new SimpleProblemMappingProcessor();
  }

  @Test
  void givenDetailWithMessageInterpolation_shouldCreateProblemObject() {
    @ProblemMapping(
        type = "https://example.com/probs/tests",
        title = "Test problem",
        status = 400,
        detail = "failed: {message}")
    class MessageException extends RuntimeException {
      MessageException(String message) {
        super(message);
      }
    }
    MessageException ex = new MessageException("boom");

    Problem problem = processor.toProblem(ex, null);

    assertThat(problem).isNotNull();
    assertThat(problem)
        .isEqualTo(
            Problem.builder()
                .type("https://example.com/probs/tests")
                .title("Test problem")
                .status(400)
                .detail("failed: boom")
                .build());
  }

  @Test
  void givenDetailWithPrivateFieldInterpolation_shouldCreateProblemObject() {
    @ProblemMapping(
        type = "https://example.com/probs/private",
        title = "Private field problem",
        status = 422,
        detail = "value: {secret}")
    class PrivateFieldException extends RuntimeException {

      private final String secret;

      PrivateFieldException(String message, String secret) {
        super(message);
        this.secret = secret;
      }
    }
    PrivateFieldException ex = new PrivateFieldException("ignored", "s3cr3t");

    Problem problem = processor.toProblem(ex, null);

    assertThat(problem).isNotNull();
    assertThat(problem)
        .isEqualTo(
            Problem.builder()
                .type("https://example.com/probs/private")
                .title("Private field problem")
                .status(422)
                .detail("value: s3cr3t")
                .build());
  }

  @ParameterizedTest
  @CsvSource(
      value = {
        "TRACE-123, https://example.com/probs/ctx/TRACE-123, Ctx TRACE-123, ctx:TRACE-123 field:v, https://example.com/instance/TRACE-123",
        "null,      https://example.com/probs/ctx/,          'Ctx ',        ctx: field:v,          https://example.com/instance/"
      },
      nullValues = "null")
  void givenTraceIdInterpolation_shouldCreateProblemObject(
      String traceId,
      String expectedType,
      String expectedTitle,
      String expectedDetail,
      String expectedInstance) {
    @ProblemMapping(
        type = "https://example.com/probs/ctx/{traceId}",
        title = "Ctx {traceId}",
        status = 418,
        detail = "ctx:{traceId} field:{value}",
        instance = "https://example.com/instance/{traceId}")
    class ContextException extends RuntimeException {

      private final String value;

      ContextException(String value) {
        super("ctx");
        this.value = value;
      }
    }
    ContextException ex = new ContextException("v");

    Problem problem = processor.toProblem(ex, new StaticProblemContext(traceId));

    assertThat(problem).isNotNull();
    assertThat(problem)
        .isEqualTo(
            Problem.builder()
                .type(expectedType)
                .title(expectedTitle)
                .status(418)
                .detail(expectedDetail)
                .instance(expectedInstance)
                .build());
  }

  @Test
  void givenNullPrivateFields_shouldOmitInterpolation() {
    @ProblemMapping(
        type = "https://example.com/probs/ext",
        title = "Extensions problem",
        status = 422,
        detail = "some detail",
        extensions = {"secret", "other"})
    class ExtensionsException extends RuntimeException {

      private final String secret;
      private final Integer other;

      ExtensionsException(String secret, Integer other) {
        super("ignored");
        this.secret = secret;
        this.other = other;
      }
    }

    ExtensionsException ex = new ExtensionsException("s", null);

    Problem problem = processor.toProblem(ex, null);

    assertThat(problem).isNotNull();
    assertThat(problem)
        .isEqualTo(
            Problem.builder()
                .type("https://example.com/probs/ext")
                .title("Extensions problem")
                .status(422)
                .detail("some detail")
                .extension("secret", "s")
                .build());
  }

  @Test
  void givenMissingPrivateFieldPlaceholder_shouldOmitInterpolation() {
    @ProblemMapping(
        type = "https://example.com/probs/multi",
        title = "Repeat",
        status = 400,
        detail = "{message} - {message} - {missing}")
    class RepeatException extends RuntimeException {
      RepeatException(String m) {
        super(m);
      }
    }
    RepeatException ex = new RepeatException("X");

    Problem problem = processor.toProblem(ex, null);

    assertThat(problem).isNotNull();
    assertThat(problem)
        .isEqualTo(
            Problem.builder()
                .type("https://example.com/probs/multi")
                .title("Repeat")
                .status(400)
                .detail("X - X - ")
                .build());
  }

  @Test
  void givenExceptionInheritance_shouldInterpolate() {
    class BaseAnnotatedException extends RuntimeException {
      BaseAnnotatedException(String m) {
        super(m);
      }
    }

    @ProblemMapping(type = "https://example.com/probs/base", title = "Base {message}", status = 400)
    class BaseWithAnnotation extends BaseAnnotatedException {
      BaseWithAnnotation(String m) {
        super(m);
      }
    }

    class ChildOfAnnotated extends BaseWithAnnotation {
      ChildOfAnnotated(String m) {
        super(m);
      }
    }
    ChildOfAnnotated ex = new ChildOfAnnotated("hello");

    Problem problem = processor.toProblem(ex, null);

    assertThat(problem).isNotNull();
    assertThat(problem)
        .isEqualTo(
            Problem.builder()
                .type("https://example.com/probs/base")
                .title("Base hello")
                .status(400)
                .build());
  }

  @Test
  void malformedPlaceholderDoesNotCrash() {
    @ProblemMapping(
        type = "https://example.com",
        title = "Bad {notClosed",
        status = 400,
        detail = "d")
    class MalformedPlaceholderException extends RuntimeException {
      MalformedPlaceholderException() {
        super("i have malformed title");
      }
    }

    MalformedPlaceholderException ex = new MalformedPlaceholderException();

    Problem p = processor.toProblem(ex, null);
    assertThat(p).isNotNull();
  }

  @Test
  void isAnnotated_returnsTrue_forDirectAnnotation() {
    @ProblemMapping(type = "type", title = "title")
    class DirectAnnotatedException extends RuntimeException {}

    Throwable ex = new DirectAnnotatedException();

    assertThat(processor.isAnnotated(ex)).isTrue();
  }

  @Test
  void isAnnotated_returnsFalse_forUnannotatedException() {
    class PlainException extends RuntimeException {}

    Throwable ex = new PlainException();

    assertThat(processor.isAnnotated(ex)).isFalse();
  }

  @Test
  void isAnnotated_returnsTrue_forInheritedAnnotation() {
    @ProblemMapping(type = "type", title = "title")
    class BaseException extends RuntimeException {}

    class SubException extends BaseException {}

    Throwable ex = new SubException();

    assertThat(processor.isAnnotated(ex)).isTrue();
  }

  @Test
  void isAnnotated_returnsFalse_forNull() {
    assertThat(processor.isAnnotated(null)).isFalse();
  }
}
