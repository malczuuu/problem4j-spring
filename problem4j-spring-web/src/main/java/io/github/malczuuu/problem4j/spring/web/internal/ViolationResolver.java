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

import static io.github.malczuuu.problem4j.spring.web.internal.MethodParameterSupport.findParameterName;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.ERRORS_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.IS_NOT_VALID_ERROR;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.VALIDATION_FAILED_DETAIL;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.spring.web.format.ProblemFormat;
import io.github.malczuuu.problem4j.spring.web.model.Violation;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.method.MethodValidationResult;
import org.springframework.validation.method.ParameterValidationResult;

/**
 * <b>For internal use only.</b>
 *
 * <p>This class is intended for internal use within the {@code problem4j-spring-*} libraries and
 * should not be used directly by external applications. The API may change or be removed without
 * notice.
 *
 * <p><b>Use at your own risk.</b>
 *
 * @implNote This is an internal API and may change at any time.
 */
public class ViolationResolver {

  /**
   * Fully qualified class name of {@code BindParam} annotation.
   *
   * <p>Cannot not use actual {@code BindParam} for backward compatibility with older versions as
   * that annotation was introduced in Spring Framework 6.1.
   */
  private static final String BIND_PARAM_FQCN = "org.springframework.web.bind.annotation.BindParam";

  /**
   * Name of the method returning the value of {@code BindParam} annotation.
   *
   * <p>Cannot not use actual {@code BindParam} for backward compatibility with older versions as
   * that annotation was introduced in Spring Framework 6.1.
   */
  private static final String BIND_PARAM_VALUE_METHOD = "value";

  private final ProblemFormat problemFormat;

  /**
   * Creates a new resolver that converts validation results into {@link ProblemBuilder}s.
   *
   * @param problemFormat formatting strategy used for the problem {@code detail} field
   */
  public ViolationResolver(ProblemFormat problemFormat) {
    this.problemFormat = problemFormat;
  }

  /**
   * Builds a {@link ProblemBuilder} for a {@link MethodValidationResult} produced by method /
   * parameter validation (e.g. {@code @Validated} on a controller). Each parameter violation is
   * mapped to a {@link Violation} whose name is the resolved method parameter name and message is
   * the constraint message.
   *
   * <p>The resulting builder has:
   *
   * <ul>
   *   <li>{@code detail} set to a formatted {@code VALIDATION_FAILED_DETAIL}
   *   <li>{@code errors} extension (key {@code ProblemSupport#ERRORS_EXTENSION}) containing a list
   *       of {@link Violation}
   * </ul>
   *
   * @param e aggregated method validation result (must not be {@code null})
   * @return a builder pre-populated with validation detail and violations extension
   * @see io.github.malczuuu.problem4j.spring.web.util.ProblemSupport
   */
  public ProblemBuilder from(MethodValidationResult e) {
    List<Violation> violations = new ArrayList<>();

    for (ParameterValidationResult result : e.getValueResults()) {
      String fieldName = findParameterName(result.getMethodParameter()).orElse(null);
      result
          .getResolvableErrors()
          .forEach(error -> violations.add(new Violation(fieldName, error.getDefaultMessage())));
    }
    return Problem.builder()
        .detail(problemFormat.formatDetail(VALIDATION_FAILED_DETAIL))
        .extension(ERRORS_EXTENSION, violations);
  }

  /**
   * Builds a {@link ProblemBuilder} from a Spring {@link BindingResult} (e.g. produced when binding
   * a {@code @ModelAttribute} fails or when {@code @Valid} detects field / global errors). Field
   * errors are translated into {@link Violation}s keyed by field name; global errors use {@code
   * null} as the field name.
   *
   * <p>The resulting builder has:
   *
   * <ul>
   *   <li>{@code detail} set to a formatted {@code VALIDATION_FAILED_DETAIL}
   *   <li>{@code errors} extension (key {@code ProblemSupport#ERRORS_EXTENSION}) containing a list
   *       of {@link Violation}
   * </ul>
   *
   * @param bindingResult the binding/validation result to convert (must not be {@code null})
   * @return a builder pre-populated with validation detail and violations extension
   * @see io.github.malczuuu.problem4j.spring.web.util.ProblemSupport#ERRORS_EXTENSION
   */
  public ProblemBuilder from(BindingResult bindingResult) {
    ArrayList<Violation> errors = new ArrayList<>();
    bindingResult.getFieldErrors().forEach(f -> errors.add(resolveFieldError(bindingResult, f)));
    bindingResult.getGlobalErrors().forEach(g -> errors.add(resolveGlobalError(g)));
    return Problem.builder()
        .detail(problemFormat.formatDetail(VALIDATION_FAILED_DETAIL))
        .extension(ERRORS_EXTENSION, errors);
  }

  /**
   * {@code isBindingFailure() == true} usually means that there was a failure in creation of object
   * from values taken out of request. Most common one is validation error or type mismatch between
   * {@code @ModelAttribute}-annotated argument and one of its values. Consider running {@code
   * WebExchangeBindExceptionWebFluxTest} or {@code MethodArgumentNotValidExceptionMvcTest} to debug
   * this feature.
   */
  private Violation resolveFieldError(BindingResult bindingResult, FieldError error) {
    Map<String, String> parametersMetadata = findParametersMetadata(bindingResult);
    String field = parametersMetadata.getOrDefault(error.getField(), error.getField());
    if (error.isBindingFailure()) {
      return new Violation(field, IS_NOT_VALID_ERROR);
    } else {
      return new Violation(field, error.getDefaultMessage());
    }
  }

  private Violation resolveGlobalError(ObjectError error) {
    return new Violation(null, error.getDefaultMessage());
  }

  /**
   * Reads metadata mapping for the target object of a BindingResult.
   *
   * @param bindingResult the BindingResult containing the target object
   * @return an unmodifiable map of parameter names to their bound names, or empty map if target is
   *     {@code null}
   */
  private Map<String, String> findParametersMetadata(BindingResult bindingResult) {
    if (bindingResult.getTarget() != null) {
      Class<?> target = bindingResult.getTarget().getClass();
      return computeConstructorMetadata(target);
    }
    return Map.of();
  }

  /**
   * Computes constructor metadata for the given class.
   *
   * @param target the class to analyze
   * @return an unmodifiable map of constructor parameter names to their bound names
   */
  private Map<String, String> computeConstructorMetadata(Class<?> target) {
    return findBindingConstructor(target)
        .filter(c -> c.getParameters().length > 0)
        .map(this::getConstructorParameterMetadata)
        .orElseGet(Map::of);
  }

  /**
   * Finds the constructor that most likely was used for binding for the given class.
   *
   * <p>For records, returns the canonical constructor. For non-records, returns the single declared
   * constructor if only one exists.
   *
   * @param target the class to inspect
   * @return an {@code Optional} containing the binding constructor if found
   */
  private Optional<Constructor<?>> findBindingConstructor(Class<?> target) {
    if (target.isRecord()) {
      Class<?>[] mainArgs =
          Arrays.stream(target.getRecordComponents())
              .map(RecordComponent::getType)
              .toArray(i -> new Class<?>[i]);
      try {
        return Optional.of(target.getDeclaredConstructor(mainArgs));
      } catch (NoSuchMethodException e) {
        return Optional.empty();
      }
    } else {
      // Non records are required to have single constructor anyway, otherwise binding will fail
      // and this code won't be called anyway
      Constructor<?>[] ctors = target.getDeclaredConstructors();
      if (ctors.length == 1) {
        return Optional.of(ctors[0]);
      }
    }
    return Optional.empty();
  }

  /**
   * Extracts parameter metadata from the given constructor.
   *
   * <p>Each constructor parameter is added with its parameter name. {@code BindParam} is taken into
   * account if present.
   *
   * @param constructor the constructor to inspect
   * @return an unmodifiable map of parameter names to their bound names
   * @see org.springframework.web.bind.annotation.BindParam
   */
  private Map<String, String> getConstructorParameterMetadata(Constructor<?> constructor) {
    Annotation[][] annotations = constructor.getParameterAnnotations();
    Parameter[] parameters = constructor.getParameters();

    Map<String, String> metadata = new HashMap<>();
    for (int i = 0; i < parameters.length; i++) {
      String rawParamName = parameters[i].getName();
      metadata.put(rawParamName, rawParamName);

      for (Annotation annotation : annotations[i]) {
        if (isBindParam(annotation)) {
          findBindParamValue(annotation)
              .ifPresent(bindParamName -> metadata.put(rawParamName, bindParamName));
        }
      }
    }
    return metadata;
  }

  /**
   * Cannot not use actual {@code BindParam} for backward compatibility with older versions as that
   * annotation was introduced in Spring Framework 6.1.
   *
   * @param annotation the annotation to check
   * @return {@code true} if the annotation is a {@code BindParam}, {@code false} otherwise
   * @see org.springframework.web.bind.annotation.BindParam
   */
  private boolean isBindParam(Annotation annotation) {
    return annotation.annotationType().getName().equals(BIND_PARAM_FQCN);
  }

  /**
   * Extracts the {@code value} attribute from a {@code BindParam} annotation via reflection.
   *
   * <p>Cannot not use actual {@code BindParam} for backward compatibility with older versions as
   * that annotation was introduced in Spring Framework 6.1.
   *
   * @param annotation the annotation to inspect
   * @return an {@code Optional} containing {@code BindParam.value} if <b>present and non-empty</b>
   * @see org.springframework.web.bind.annotation.BindParam
   */
  private Optional<String> findBindParamValue(Annotation annotation) {
    try {
      Method method = annotation.getClass().getMethod(BIND_PARAM_VALUE_METHOD);
      Object value = method.invoke(annotation);
      return Optional.ofNullable(value).map(Object::toString).filter(StringUtils::hasLength);
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
      // ignore reflective call issues
    }
    return Optional.empty();
  }
}
