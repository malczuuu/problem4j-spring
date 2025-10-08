package io.github.malczuuu.problem4j.spring.webflux;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.malczuuu.problem4j.spring.web.CachingProblemResolverStore;
import io.github.malczuuu.problem4j.spring.web.annotation.DefaultProblemMappingProcessor;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

class ExceptionWebFluxAdviceTest {

  private ExceptionWebFluxAdvice advice;

  private AtomicInteger hits;

  @BeforeEach
  void beforeEach() {
    hits = new AtomicInteger(0);
    advice =
        new ExceptionWebFluxAdvice(
            new DefaultProblemMappingProcessor(),
            new CachingProblemResolverStore(List.of()),
            List.of((context, problem, ex, headers, status, exchange) -> hits.incrementAndGet()));
  }

  @Test
  void whileHandlingException_shouldHitInspector() {
    advice.handleException(
        new RuntimeException(),
        MockServerWebExchange.from(MockServerHttpRequest.get("/test").build()));

    assertThat(hits.get()).isEqualTo(1);
  }
}
