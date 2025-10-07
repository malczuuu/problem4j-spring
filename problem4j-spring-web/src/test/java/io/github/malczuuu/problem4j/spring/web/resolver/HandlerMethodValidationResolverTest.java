package io.github.malczuuu.problem4j.spring.web.resolver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.format.IdentityProblemFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.validation.method.MethodValidationResult;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

class HandlerMethodValidationResolverTest {

  private HandlerMethodValidationResolver handlerMethodValidationResolver;

  @BeforeEach
  void beforeEach() {
    handlerMethodValidationResolver =
        new HandlerMethodValidationResolver(new IdentityProblemFormat());
  }

  @Test
  void givenHandlerMethodValidationException_shouldGenerateProblem() {
    MethodValidationResult mockMethodValidationResult = mock(MethodValidationResult.class);
    HandlerMethodValidationException ex =
        new HandlerMethodValidationException(mockMethodValidationResult);

    Problem problem =
        handlerMethodValidationResolver.resolveProblem(
            ProblemContext.ofTraceId("traceId"), ex, null, HttpStatus.BAD_REQUEST);

    assertEquals(Problem.BLANK_TYPE, problem.getType());
    assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), problem.getTitle());
    assertEquals(HttpStatus.BAD_REQUEST.value(), problem.getStatus());
  }
}
