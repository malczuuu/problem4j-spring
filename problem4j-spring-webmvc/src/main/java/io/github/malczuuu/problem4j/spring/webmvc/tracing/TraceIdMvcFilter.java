package io.github.malczuuu.problem4j.spring.webmvc.tracing;

import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.internal.InstanceSupport;
import io.github.malczuuu.problem4j.spring.web.tracing.TracingSupport;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

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
        InstanceSupport.overrideInstance(
            instanceOverride, ProblemContext.builder().traceId(traceId).build());

    request.setAttribute(TracingSupport.TRACE_ID, traceId);
    request.setAttribute(TracingSupport.INSTANCE_OVERRIDE, instanceOverrideValue);
    response.setHeader(tracingHeaderName, traceId);

    filterChain.doFilter(request, response);
  }
}
