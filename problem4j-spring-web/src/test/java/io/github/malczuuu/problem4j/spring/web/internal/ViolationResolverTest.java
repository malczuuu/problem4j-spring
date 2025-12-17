/*
 * Copyright (c) 2025 Damian Malczewski
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * SPDX-License-Identifier: MIT
 */
package io.github.malczuuu.problem4j.spring.web.internal;

import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.ERRORS_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.IS_NOT_VALID_ERROR;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.VALIDATION_FAILED_DETAIL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.spring.web.format.IdentityProblemFormat;
import io.github.malczuuu.problem4j.spring.web.model.Violation;
import java.lang.reflect.Method;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.annotation.RequestParam;

class ViolationResolverTest {

  private final ViolationResolver resolver = new ViolationResolver(new IdentityProblemFormat());

  @Test
  void givenBindingResultForValidationError_shouldResolveViolation() {
    TestObject target = new TestObject();
    BindingResult bindingResult = new BeanPropertyBindingResult(target, "testObject");
    bindingResult.addError(new FieldError("testObject", "name", "must not be blank"));
    bindingResult.addError(new ObjectError("testObject", "object invalid"));

    Problem problem = resolver.from(bindingResult).build();

    assertThat(problem.getDetail()).isEqualTo(VALIDATION_FAILED_DETAIL);

    @SuppressWarnings("unchecked")
    List<Violation> violations = (List<Violation>) problem.getExtensionValue(ERRORS_EXTENSION);
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

    Problem problem = resolver.from(bindingResult).build();

    assertThat(problem.getDetail()).isEqualTo(VALIDATION_FAILED_DETAIL);

    @SuppressWarnings("unchecked")
    List<Violation> violations = (List<Violation>) problem.getExtensionValue(ERRORS_EXTENSION);
    assertThat(violations).containsExactly(new Violation("age", IS_NOT_VALID_ERROR));
  }

  @Test
  void givenBindingResultWithoutErrors_shouldNotReturnViolations() {
    BindingResult bindingResult = new BeanPropertyBindingResult(new TestObject(), "testObject");

    Problem problem = resolver.from(bindingResult).build();

    @SuppressWarnings("unchecked")
    List<Violation> violations = (List<Violation>) problem.getExtensionValue(ERRORS_EXTENSION);
    assertThat(violations).isEmpty();
  }

  @Test
  void givenMethodValidationException_shouldResolveViolations() throws NoSuchMethodException {
    Method method =
        SampleValidatedMethods.class.getDeclaredMethod("sample", String.class, String.class);
    MethodParameter firstParam = new MethodParameter(method, 0);
    MethodParameter secondParam = new MethodParameter(method, 1);

    ParameterValidationResult firstResult = mock(ParameterValidationResult.class);
    when(firstResult.getMethodParameter()).thenReturn(firstParam);
    when(firstResult.getResolvableErrors())
        .thenReturn(List.of(new ObjectError("first", "must not be null")));

    ParameterValidationResult secondResult = mock(ParameterValidationResult.class);
    when(secondResult.getMethodParameter()).thenReturn(secondParam);
    when(secondResult.getResolvableErrors())
        .thenReturn(List.of(new ObjectError("second", "size must be between 3 and 10")));

    MethodValidationException ex = mock(MethodValidationException.class);
    when(ex.getValueResults()).thenReturn(List.of(firstResult, secondResult));

    Problem problem = resolver.from(ex).build();

    assertThat(problem.getDetail()).isEqualTo(VALIDATION_FAILED_DETAIL);

    @SuppressWarnings("unchecked")
    List<Violation> violations = (List<Violation>) problem.getExtensionValue(ERRORS_EXTENSION);
    assertThat(violations).hasSize(2);
    assertThat(violations.get(0)).isEqualTo(new Violation("p1", "must not be null"));

    assertThat(violations.get(1).getField()).isNull();
    assertThat(violations.get(1).getError()).isEqualTo("size must be between 3 and 10");
  }

  static class TestObject {
    private String name;
    private Integer age;
  }

  static class SampleValidatedMethods {
    void sample(@RequestParam("p1") String first, String second) {}
  }
}
