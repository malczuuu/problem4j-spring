package io.github.malczuuu.problem4j.spring.webflux.context;

import static io.github.malczuuu.problem4j.spring.web.context.ContextSupport.PROBLEM_CONTEXT;
import static io.github.malczuuu.problem4j.spring.web.context.ContextSupport.TRACE_ID;
import static io.github.malczuuu.problem4j.spring.web.context.ContextSupport.getRandomTraceId;

import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContextSettings;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * {@link WebFilter} that ensures each request processed by a WebFlux application has an associated
 * context and trace identifier.
 *
 * <p>The filter reads the trace ID from a configured HTTP header, generates one if missing, and
 * stores it in the {@link ServerWebExchange} attributes, response headers, and Reactor context for
 * downstream access.
 */
public class ProblemContextWebFluxFilter implements WebFilter {

  private final ProblemContextSettings settings;

  public ProblemContextWebFluxFilter(ProblemContextSettings settings) {
    this.settings = settings;
  }

  /**
   * Applies the filter logic.
   *
   * <p>Ensures that a trace ID is available for the request, stores it in exchange attributes, adds
   * it to the response headers, and propagates it through the Reactor context.
   *
   * <p>Also adds attribute holding {@code instance} field to override for {@code Problem} response
   * body.
   *
   * @param exchange the current server exchange
   * @param chain the filter chain to continue processing
   * @return a completion signal when request processing is finished
   */
  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    String traceId = readTraceId(exchange);
    ProblemContext context = ProblemContext.builder().traceId(traceId).build();

    exchange.getAttributes().put(TRACE_ID, traceId);
    exchange.getAttributes().put(PROBLEM_CONTEXT, context);

    if (settings.getTracingHeaderName() != null) {
      exchange.getResponse().getHeaders().set(settings.getTracingHeaderName(), traceId);
    }

    return chain.filter(exchange).contextWrite(ctx -> ctx.put(TRACE_ID, traceId));
  }

  /**
   * Reads the trace ID from the request headers or generates a new one if missing.
   *
   * @param exchange the current server exchange
   * @return existing or newly generated trace identifier
   */
  private String readTraceId(ServerWebExchange exchange) {
    String traceId = exchange.getRequest().getHeaders().getFirst(settings.getTracingHeaderName());
    if (!StringUtils.hasLength(traceId)) {
      traceId = getRandomTraceId();
    }
    return traceId;
  }
}
