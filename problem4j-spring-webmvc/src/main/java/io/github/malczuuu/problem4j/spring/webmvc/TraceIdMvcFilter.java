package io.github.malczuuu.problem4j.spring.webmvc;

import io.github.malczuuu.problem4j.spring.web.util.InstanceSupport;
import io.github.malczuuu.problem4j.spring.web.util.StaticProblemContext;
import io.github.malczuuu.problem4j.spring.web.util.TracingSupport;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * String traceId = readTraceId(exchange);
 *
 * <p>String instanceOverrideValue = InstanceSupport.overrideInstance(instanceOverride, new
 * StaticProblemContext(traceId));
 *
 * <p>exchange.getAttributes().put(TracingSupport.TRACE_ID_ATTR, traceId);
 * exchange.getAttributes().put(TracingSupport.INSTANCE_OVERRIDE_ATTR, instanceOverrideValue);
 *
 * <p>exchange.getResponse().getHeaders().set(tracingHeaderName, traceId);
 */
public class TraceIdMvcFilter extends OncePerRequestFilter {

  private final String tracingHeaderName;
  private final String instanceOverride;

  public TraceIdMvcFilter(String tracingHeaderName, String instanceOverride) {
    this.tracingHeaderName = tracingHeaderName;
    this.instanceOverride = instanceOverride;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String traceId = request.getHeader(tracingHeaderName);
    if (!StringUtils.hasLength(traceId)) {
      traceId = TracingSupport.getRandomTraceId();
    }

    String instanceOverrideValue =
        InstanceSupport.overrideInstance(instanceOverride, new StaticProblemContext(traceId));

    request.setAttribute(TracingSupport.TRACE_ID_ATTR, traceId);
    request.setAttribute(TracingSupport.INSTANCE_OVERRIDE_ATTR, instanceOverrideValue);
    response.setHeader(tracingHeaderName, traceId);

    filterChain.doFilter(request, response);
  }
}
