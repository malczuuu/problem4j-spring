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
package io.github.problem4j.spring.web.internal;

import java.lang.annotation.Annotation;
import java.util.Optional;
import org.springframework.core.MethodParameter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.SessionAttribute;

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
public final class MethodParameterSupport {

  /**
   * Resolve a stable logical name for a method parameter, honoring supported Spring binding
   * annotations. If an annotation supplies an explicit {@code name} or {@code value}, that wins;
   * otherwise falls back to the parameter's discovered name. Unknown or unsupported annotations are
   * ignored.
   *
   * @param parameter Spring {@link MethodParameter} (may be {@code null})
   * @return optional parameter name; empty if the input is {@code null}
   */
  public static Optional<String> findParameterName(MethodParameter parameter) {
    if (parameter == null) {
      return Optional.empty();
    }

    Annotation[] annotations = parameter.getParameterAnnotations();
    String fieldName = parameter.getParameterName();
    for (Annotation annotation : annotations) {
      if (annotation instanceof PathVariable pathVariable) {
        return Optional.ofNullable(findPathVariableName(pathVariable, fieldName));
      } else if (annotation instanceof RequestParam requestParam) {
        return Optional.ofNullable(findRequestParamName(requestParam, fieldName));
      } else if (annotation instanceof RequestPart requestPart) {
        return Optional.ofNullable(findRequestPartName(requestPart, fieldName));
      } else if (annotation instanceof RequestHeader requestHeader) {
        return Optional.ofNullable(findRequestHeaderName(requestHeader, fieldName));
      } else if (annotation instanceof CookieValue cookieValue) {
        return Optional.ofNullable(findCookieValueName(cookieValue, fieldName));
      } else if (annotation instanceof SessionAttribute sessionAttribute) {
        return Optional.ofNullable(findSessionAttributeName(sessionAttribute, fieldName));
      } else if (annotation instanceof RequestAttribute requestAttribute) {
        return Optional.ofNullable(findRequestAttributeName(requestAttribute, fieldName));
      } else if (annotation instanceof MatrixVariable matrixVariable) {
        return Optional.ofNullable(findMatrixVariableName(matrixVariable, fieldName));
      }
    }
    return Optional.ofNullable(fieldName);
  }

  /**
   * Derive the effective name for a {@link PathVariable}, preferring {@code name} then {@code
   * value}.
   *
   * @param annotation path variable annotation
   * @param defaultName fallback (parameter name)
   * @return resolved name or fallback
   */
  private static String findPathVariableName(PathVariable annotation, String defaultName) {
    String name = annotation.name();
    if (!StringUtils.hasLength(name)) {
      name = annotation.value();
    }
    return StringUtils.hasLength(name) ? name : defaultName;
  }

  /**
   * Derive the effective name for a {@link RequestParam}, preferring {@code name} then {@code
   * value}.
   *
   * @param annotation request param annotation
   * @param defaultName fallback (parameter name)
   * @return resolved name or fallback
   */
  private static String findRequestParamName(RequestParam annotation, String defaultName) {
    String name = annotation.name();
    if (!StringUtils.hasLength(name)) {
      name = annotation.value();
    }
    return StringUtils.hasLength(name) ? name : defaultName;
  }

  /**
   * Derive the effective name for a {@link RequestPart}, preferring {@code name} then {@code
   * value}.
   *
   * @param annotation request part annotation
   * @param defaultName fallback (parameter name)
   * @return resolved name or fallback
   */
  private static String findRequestPartName(RequestPart annotation, String defaultName) {
    String name = annotation.name();
    if (!StringUtils.hasLength(name)) {
      name = annotation.value();
    }
    return StringUtils.hasLength(name) ? name : defaultName;
  }

  /**
   * Derive the effective name for a {@link RequestHeader}, preferring {@code name} then {@code
   * value}.
   *
   * @param annotation request header annotation
   * @param defaultName fallback (parameter name)
   * @return resolved name or fallback
   */
  private static String findRequestHeaderName(RequestHeader annotation, String defaultName) {
    String name = annotation.name();
    if (!StringUtils.hasLength(name)) {
      name = annotation.value();
    }
    return StringUtils.hasLength(name) ? name : defaultName;
  }

  /**
   * Derive the effective name for a {@link CookieValue}, preferring {@code name} then {@code
   * value}.
   *
   * @param annotation cookie value annotation
   * @param defaultName fallback (parameter name)
   * @return resolved name or fallback
   */
  private static String findCookieValueName(CookieValue annotation, String defaultName) {
    String name = annotation.name();
    if (!StringUtils.hasLength(name)) {
      name = annotation.value();
    }
    return StringUtils.hasLength(name) ? name : defaultName;
  }

  /**
   * Derive the effective name for a {@link SessionAttribute}, preferring {@code name} then {@code
   * value}.
   *
   * @param annotation session attribute annotation
   * @param defaultName fallback (parameter name)
   * @return resolved name or fallback
   */
  private static String findSessionAttributeName(SessionAttribute annotation, String defaultName) {
    String name = annotation.name();
    if (!StringUtils.hasLength(name)) {
      name = annotation.value();
    }
    return StringUtils.hasLength(name) ? name : defaultName;
  }

  /**
   * Derive the effective name for a {@link RequestAttribute}, preferring {@code name} then {@code
   * value}.
   *
   * @param annotation request attribute annotation
   * @param defaultName fallback (parameter name)
   * @return resolved name or fallback
   */
  private static String findRequestAttributeName(RequestAttribute annotation, String defaultName) {
    String name = annotation.name();
    if (!StringUtils.hasLength(name)) {
      name = annotation.value();
    }
    return StringUtils.hasLength(name) ? name : defaultName;
  }

  /**
   * Derive the effective name for a {@link MatrixVariable}, preferring {@code name} then {@code
   * value}.
   *
   * @param annotation matrix variable annotation
   * @param defaultName fallback (parameter name)
   * @return resolved name or fallback
   */
  private static String findMatrixVariableName(MatrixVariable annotation, String defaultName) {
    String name = annotation.name();
    if (!StringUtils.hasLength(name)) {
      name = annotation.value();
    }
    return StringUtils.hasLength(name) ? name : defaultName;
  }

  /** Utility class; no instances. */
  private MethodParameterSupport() {}
}
