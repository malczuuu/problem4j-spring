package io.github.malczuuu.problem4j.spring.webmvc;

import io.github.malczuuu.problem4j.spring.web.util.TracingSupport;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class TraceIdMvcFilter extends OncePerRequestFilter {

  private final String tracingHeaderName;

  public TraceIdMvcFilter(String tracingHeaderName) {
    this.tracingHeaderName = tracingHeaderName;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String traceId = request.getHeader(tracingHeaderName);
    if (!StringUtils.hasLength(traceId)) {
      traceId = TracingSupport.getRandomTraceId();
    }

    request.setAttribute(TracingSupport.TRACE_ID_ATTR, traceId);
    response.setHeader(tracingHeaderName, traceId);

    filterChain.doFilter(request, response);
  }
}
