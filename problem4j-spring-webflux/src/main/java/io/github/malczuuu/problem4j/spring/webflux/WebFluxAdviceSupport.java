package io.github.malczuuu.problem4j.spring.webflux;

import static io.github.malczuuu.problem4j.spring.web.context.ContextSupport.TRACE_ID;

import org.slf4j.Logger;
import org.springframework.web.server.ServerWebExchange;

class WebFluxAdviceSupport {

  static void logAdviceException(
      Logger log, Exception ex, ServerWebExchange exchange, Exception e) {
    log.warn(
        "Unable to resolve problem response (method={}, path={}, traceId={}, message={}, originalException=[{} : {}])",
        exchange.getRequest().getMethod(),
        exchange.getRequest().getPath(),
        exchange.getAttribute(TRACE_ID),
        e.getMessage(),
        ex.getClass().getName(),
        ex.getMessage(),
        e);
  }
}
