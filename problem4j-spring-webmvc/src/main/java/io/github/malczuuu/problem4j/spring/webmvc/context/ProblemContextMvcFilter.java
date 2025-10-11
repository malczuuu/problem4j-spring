package io.github.malczuuu.problem4j.spring.webmvc.context;

import static io.github.malczuuu.problem4j.spring.web.context.ContextSupport.PROBLEM_CONTEXT;
import static io.github.malczuuu.problem4j.spring.web.context.ContextSupport.TRACE_ID;

import io.github.malczuuu.problem4j.spring.web.context.ContextSupport;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContextSettings;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * {@link OncePerRequestFilter} that ensures each request processed by a Web MVC application has an
 * associated context and trace identifier.
 *
 * <p>The filter reads the trace ID from a configured HTTP header, generates one if missing, and
 * stores it in the {@link HttpServletRequest} attributes, response headers for downstream access.
 */
public class ProblemContextMvcFilter extends OncePerRequestFilter {

  private final ProblemContextSettings settings;

  public ProblemContextMvcFilter(ProblemContextSettings settings) {
    this.settings = settings;
  }

  /**
   * Applies the filter logic.
   *
   * <p>Ensures that a trace ID is available for the request, stores it in exchange attributes, adds
   * it to the response headers, and propagates it through.
   *
   * <p>Also adds attribute holding {@code instance} field to override for {@code Problem} response
   * body.
   *
   * @param request the current server request
   * @param response the current server response
   * @param filterChain the filter chain to continue processing
   */
  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String traceId = readTraceId(request);
    ProblemContext context = ProblemContext.builder().traceId(traceId).build();

    request.setAttribute(TRACE_ID, traceId);
    request.setAttribute(PROBLEM_CONTEXT, context);

    if (settings.getTracingHeaderName() != null) {
      response.setHeader(settings.getTracingHeaderName(), traceId);
    }

    filterChain.doFilter(request, response);
  }

  /**
   * Reads the trace ID from the request headers or generates a new one if missing.
   *
   * @param request the current server request
   * @return existing or newly generated trace identifier
   */
  private String readTraceId(HttpServletRequest request) {
    String traceId = request.getHeader(settings.getTracingHeaderName());
    if (!StringUtils.hasLength(traceId)) {
      traceId = ContextSupport.getRandomTraceId();
    }
    return traceId;
  }
}
