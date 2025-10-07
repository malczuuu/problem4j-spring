package io.github.malczuuu.problem4j.spring.webflux.tracing;

import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.internal.InstanceSupport;
import io.github.malczuuu.problem4j.spring.web.tracing.TracingSupport;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * {@link WebFilter} that ensures each request processed by a WebFlux application has an associated
 * trace identifier.
 *
 * <p>The filter reads the trace ID from a configured HTTP header, generates one if missing, and
 * stores it in the {@link ServerWebExchange} attributes, response headers, and Reactor context for
 * downstream access.
 */
public class TraceIdWebFluxFilter implements WebFilter {

  private final String tracingHeaderName;
  private final String instanceOverride;

  /**
   * Creates a new filter using the given tracing header name and instance override.
   *
   * @param tracingHeaderName name of the HTTP header carrying the trace ID
   * @param instanceOverride template for overriding {@code Problem.instance} field
   */
  public TraceIdWebFluxFilter(String tracingHeaderName, String instanceOverride) {
    this.tracingHeaderName = tracingHeaderName;
    this.instanceOverride = instanceOverride;
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
    String instanceOverrideValue = InstanceSupport.overrideInstance(instanceOverride, context);

    exchange.getAttributes().put(TracingSupport.TRACE_ID_ATTR, traceId);
    exchange.getAttributes().put(TracingSupport.INSTANCE_OVERRIDE_ATTR, instanceOverrideValue);

    exchange.getResponse().getHeaders().set(tracingHeaderName, traceId);

    return chain
        .filter(exchange)
        .contextWrite(ctx -> ctx.put(TracingSupport.TRACE_ID_ATTR, traceId));
  }

  /**
   * Reads the trace ID from the request headers or generates a new one if missing.
   *
   * @param exchange the current server exchange
   * @return existing or newly generated trace identifier
   */
  private String readTraceId(ServerWebExchange exchange) {
    String traceId = exchange.getRequest().getHeaders().getFirst(tracingHeaderName);
    if (!StringUtils.hasLength(traceId)) {
      traceId = TracingSupport.getRandomTraceId();
    }
    return traceId;
  }
}
