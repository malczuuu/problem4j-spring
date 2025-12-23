package io.github.problem4j.spring.web.parameter;

import static io.github.problem4j.spring.web.util.ProblemSupport.IS_NOT_VALID_ERROR;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

class DefaultBindingResultSupportTest {

  private final BindingResultSupport support = new DefaultBindingResultSupport();

  @Test
  void givenBindingResultForValidationError_shouldResolveViolation() {
    TestObject target = new TestObject();
    BindingResult bindingResult = new BeanPropertyBindingResult(target, "testObject");
    bindingResult.addError(new FieldError("testObject", "name", "must not be blank"));
    bindingResult.addError(new ObjectError("testObject", "object invalid"));

    List<Violation> violations = support.fetchViolations(bindingResult);

    assertThat(violations)
        .containsExactly(
            new Violation("name", "must not be blank"), new Violation(null, "object invalid"));
  }

  @Test
  void givenBindingResultForBindingError_shouldResolveViolation() {
    TestObject target = new TestObject();
    BindingResult bindingResult = new BeanPropertyBindingResult(target, "testObject");

    bindingResult.addError(
        new FieldError("testObject", "age", target, true, null, null, "should be ignored message"));

    List<Violation> violations = support.fetchViolations(bindingResult);

    assertThat(violations).containsExactly(new Violation("age", IS_NOT_VALID_ERROR));
  }

  @Test
  void givenBindingResultWithoutErrors_shouldNotReturnViolations() {
    BindingResult bindingResult = new BeanPropertyBindingResult(new TestObject(), "testObject");

    List<Violation> violations = support.fetchViolations(bindingResult);

    assertThat(violations).isEmpty();
  }

  static class TestObject {
    private String name;
    private Integer age;
  }
}
