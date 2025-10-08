package io.github.malczuuu.problem4j.spring.webmvc;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.malczuuu.problem4j.spring.web.CachingProblemResolverStore;
import io.github.malczuuu.problem4j.spring.web.annotation.DefaultProblemMappingProcessor;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.ServletWebRequest;

class ExceptionMvcAdviceTest {

  private ExceptionMvcAdvice advice;

  private AtomicInteger hits;

  @BeforeEach
  void beforeEach() {
    hits = new AtomicInteger(0);
    advice =
        new ExceptionMvcAdvice(
            new DefaultProblemMappingProcessor(),
            new CachingProblemResolverStore(List.of()),
            List.of((context, problem, ex, headers, status, exchange) -> hits.incrementAndGet()));
  }

  @Test
  void whileHandlingException_shouldHitInspector() {
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/test");
    MockHttpServletResponse response = new MockHttpServletResponse();

    advice.handleException(
        new ConstraintViolationException("message", Set.of()),
        new ServletWebRequest(request, response));

    assertThat(hits.get()).isEqualTo(1);
  }
}
