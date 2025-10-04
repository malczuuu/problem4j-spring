package io.github.malczuuu.problem4j.spring.webflux;

import io.github.malczuuu.problem4j.spring.web.util.TracingSupport;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

public class TraceIdFluxFilter implements WebFilter {

  private final String tracingHeaderName;

  public TraceIdFluxFilter(String tracingHeaderName) {
    this.tracingHeaderName = tracingHeaderName;
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

    String traceId = readTraceId(exchange);

    exchange.getAttributes().put(TracingSupport.TRACE_ID_ATTR, traceId);
    exchange.getResponse().getHeaders().set(tracingHeaderName, traceId);

    return chain
        .filter(exchange)
        .contextWrite(ctx -> ctx.put(TracingSupport.TRACE_ID_ATTR, traceId));
  }

  private String readTraceId(ServerWebExchange exchange) {
    String traceId = exchange.getRequest().getHeaders().getFirst(tracingHeaderName);
    if (!StringUtils.hasLength(traceId)) {
      traceId = TracingSupport.getRandomTraceId();
    }
    return traceId;
  }
}
