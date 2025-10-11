package io.github.malczuuu.problem4j.spring.webflux;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.malczuuu.problem4j.spring.web.processor.IdentityProblemPostProcessor;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.codec.DecodingException;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

class DecodingExceptionWebFluxAdviceTest {

  private DecodingExceptionWebFluxAdvice advice;

  private AtomicInteger hits;

  @BeforeEach
  void beforeEach() {
    hits = new AtomicInteger(0);
    advice =
        new DecodingExceptionWebFluxAdvice(
            new IdentityProblemPostProcessor(),
            List.of((context, problem, ex, headers, status, exchange) -> hits.incrementAndGet()));
  }

  @Test
  void whileHandlingException_shouldHitInspector() {
    advice.handleDecodingException(
        new DecodingException("message"),
        MockServerWebExchange.from(MockServerHttpRequest.get("/test").build()));

    assertThat(hits.get()).isEqualTo(1);
  }
}
