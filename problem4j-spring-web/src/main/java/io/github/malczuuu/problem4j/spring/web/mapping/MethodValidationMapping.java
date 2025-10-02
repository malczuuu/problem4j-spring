package io.github.malczuuu.problem4j.spring.web.mapping;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.format.DetailFormat;
import io.github.malczuuu.problem4j.spring.web.model.Violation;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.util.StringUtils;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.SessionAttribute;

public class MethodValidationMapping implements ExceptionMapping {

  private final DetailFormat detailFormat;

  public MethodValidationMapping(DetailFormat detailFormat) {
    this.detailFormat = detailFormat;
  }

  @Override
  public Class<MethodValidationException> getExceptionClass() {
    return MethodValidationException.class;
  }

  @Override
  public Problem map(Exception ex, HttpHeaders headers, HttpStatusCode status) {
    MethodValidationException e = (MethodValidationException) ex;
    return from(e).status(ProblemStatus.BAD_REQUEST).build();
  }

  private ProblemBuilder from(MethodValidationException e) {
    List<Violation> violations = new ArrayList<>();

    for (ParameterValidationResult result : e.getParameterValidationResults()) {
      Annotation[] parameterAnnotations = findParameterAnnotations(result);
      String fieldName = findViolationFieldName(result, parameterAnnotations);
      result
          .getResolvableErrors()
          .forEach(error -> violations.add(new Violation(fieldName, error.getDefaultMessage())));
    }
    return Problem.builder()
        .detail(detailFormat.format("Validation failed"))
        .extension("errors", violations);
  }

  private Annotation[] findParameterAnnotations(ParameterValidationResult result) {
    Method method = result.getMethodParameter().getMethod();

    Annotation[][] methodParameterAnnotations =
        method != null ? method.getParameterAnnotations() : null;

    return methodParameterAnnotations != null
        ? methodParameterAnnotations[result.getMethodParameter().getParameterIndex()]
        : new Annotation[0];
  }

  private String findViolationFieldName(
      ParameterValidationResult result, Annotation[] parameterAnnotations) {
    String fieldName = result.getMethodParameter().getParameterName();

    for (Annotation annotation : parameterAnnotations) {
      if (annotation instanceof PathVariable pathVariable) {
        return findPathVariableName(pathVariable, fieldName);
      } else if (annotation instanceof RequestParam requestParam) {
        return findRequestParamName(requestParam, fieldName);
      } else if (annotation instanceof RequestPart requestPart) {
        return findRequestPartName(requestPart, fieldName);
      } else if (annotation instanceof RequestHeader requestHeader) {
        return findRequestHeaderName(requestHeader, fieldName);
      } else if (annotation instanceof CookieValue cookieValue) {
        return findCookieValueName(cookieValue, fieldName);
      } else if (annotation instanceof SessionAttribute sessionAttribute) {
        return findSessionAttributeName(sessionAttribute, fieldName);
      } else if (annotation instanceof RequestAttribute requestAttribute) {
        return findRequestAttributeName(requestAttribute, fieldName);
      } else if (annotation instanceof MatrixVariable matrixVariable) {
        return findMatrixVariableName(matrixVariable, fieldName);
      }
    }
    return fieldName;
  }

  private String findPathVariableName(PathVariable annotation, String defaultName) {
    String name = annotation.name();
    if (!StringUtils.hasLength(name)) {
      name = annotation.value();
    }
    return StringUtils.hasLength(name) ? name : defaultName;
  }

  private String findRequestParamName(RequestParam annotation, String defaultName) {
    String name = annotation.name();
    if (!StringUtils.hasLength(name)) {
      name = annotation.value();
    }
    return StringUtils.hasLength(name) ? name : defaultName;
  }

  private String findRequestPartName(RequestPart annotation, String defaultName) {
    String name = annotation.name();
    if (!StringUtils.hasLength(name)) {
      name = annotation.value();
    }
    return StringUtils.hasLength(name) ? name : defaultName;
  }

  private String findRequestHeaderName(RequestHeader annotation, String defaultName) {
    String name = annotation.name();
    if (!StringUtils.hasLength(name)) {
      name = annotation.value();
    }
    return StringUtils.hasLength(name) ? name : defaultName;
  }

  private String findCookieValueName(CookieValue annotation, String defaultName) {
    String name = annotation.name();
    if (!StringUtils.hasLength(name)) {
      name = annotation.value();
    }
    return StringUtils.hasLength(name) ? name : defaultName;
  }

  private String findSessionAttributeName(SessionAttribute annotation, String defaultName) {
    String name = annotation.name();
    if (!StringUtils.hasLength(name)) {
      name = annotation.value();
    }
    return StringUtils.hasLength(name) ? name : defaultName;
  }

  private String findRequestAttributeName(RequestAttribute annotation, String defaultName) {
    String name = annotation.name();
    if (!StringUtils.hasLength(name)) {
      name = annotation.value();
    }
    return StringUtils.hasLength(name) ? name : defaultName;
  }

  private String findMatrixVariableName(MatrixVariable annotation, String defaultName) {
    String name = annotation.name();
    if (!StringUtils.hasLength(name)) {
      name = annotation.value();
    }
    return StringUtils.hasLength(name) ? name : defaultName;
  }
}
