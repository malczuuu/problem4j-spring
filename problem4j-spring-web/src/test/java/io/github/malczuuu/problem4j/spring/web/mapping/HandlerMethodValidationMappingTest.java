package io.github.malczuuu.problem4j.spring.web.mapping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import io.github.malczuuu.problem4j.core.Problem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.validation.method.MethodValidationResult;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

class HandlerMethodValidationMappingTest {

  private HandlerMethodValidationMapping mapping;

  @BeforeEach
  void beforeEach() {
    mapping = new HandlerMethodValidationMapping();
  }

  @Test
  void givenHandlerMethodValidationException_shouldGenerateProblem() {
    MethodValidationResult mockMethodValidationResult = mock(MethodValidationResult.class);
    HandlerMethodValidationException ex =
        new HandlerMethodValidationException(mockMethodValidationResult);

    Problem problem = mapping.map(ex, null, HttpStatus.BAD_REQUEST);

    assertEquals(Problem.BLANK_TYPE, problem.getType());
    assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), problem.getTitle());
    assertEquals(HttpStatus.BAD_REQUEST.value(), problem.getStatus());
  }
}
