package io.github.malczuuu.problem4j.spring.web.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;
import org.jetbrains.annotations.ApiStatus;
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
 * <p><b>Use at your own risk</b>
 *
 * @implNote This is an internal API and may change at any time.
 * @see ApiStatus.Internal
 */
@ApiStatus.Internal
public final class MethodParameterSupport {

  public static Optional<String> findParameterName(MethodParameter parameter) {
    if (parameter == null) {
      return Optional.empty();
    }

    Method method = parameter.getMethod();

    Annotation[][] allParametersAnnotations =
        method != null ? method.getParameterAnnotations() : null;

    Annotation[] targetParameterAnnotations =
        allParametersAnnotations != null
            ? allParametersAnnotations[parameter.getParameterIndex()]
            : new Annotation[0];

    String fieldName = parameter.getParameterName();
    for (Annotation annotation : targetParameterAnnotations) {
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

  private static String findPathVariableName(PathVariable annotation, String defaultName) {
    String name = annotation.name();
    if (!StringUtils.hasLength(name)) {
      name = annotation.value();
    }
    return StringUtils.hasLength(name) ? name : defaultName;
  }

  private static String findRequestParamName(RequestParam annotation, String defaultName) {
    String name = annotation.name();
    if (!StringUtils.hasLength(name)) {
      name = annotation.value();
    }
    return StringUtils.hasLength(name) ? name : defaultName;
  }

  private static String findRequestPartName(RequestPart annotation, String defaultName) {
    String name = annotation.name();
    if (!StringUtils.hasLength(name)) {
      name = annotation.value();
    }
    return StringUtils.hasLength(name) ? name : defaultName;
  }

  private static String findRequestHeaderName(RequestHeader annotation, String defaultName) {
    String name = annotation.name();
    if (!StringUtils.hasLength(name)) {
      name = annotation.value();
    }
    return StringUtils.hasLength(name) ? name : defaultName;
  }

  private static String findCookieValueName(CookieValue annotation, String defaultName) {
    String name = annotation.name();
    if (!StringUtils.hasLength(name)) {
      name = annotation.value();
    }
    return StringUtils.hasLength(name) ? name : defaultName;
  }

  private static String findSessionAttributeName(SessionAttribute annotation, String defaultName) {
    String name = annotation.name();
    if (!StringUtils.hasLength(name)) {
      name = annotation.value();
    }
    return StringUtils.hasLength(name) ? name : defaultName;
  }

  private static String findRequestAttributeName(RequestAttribute annotation, String defaultName) {
    String name = annotation.name();
    if (!StringUtils.hasLength(name)) {
      name = annotation.value();
    }
    return StringUtils.hasLength(name) ? name : defaultName;
  }

  private static String findMatrixVariableName(MatrixVariable annotation, String defaultName) {
    String name = annotation.name();
    if (!StringUtils.hasLength(name)) {
      name = annotation.value();
    }
    return StringUtils.hasLength(name) ? name : defaultName;
  }

  private MethodParameterSupport() {}
}
