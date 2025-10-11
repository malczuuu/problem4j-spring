package io.github.malczuuu.problem4j.spring.webmvc;

import static io.github.malczuuu.problem4j.spring.web.context.ContextSupport.PROBLEM_CONTEXT;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.resolveStatus;
import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.spring.web.ProblemResolverStore;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.processor.ProblemPostProcessor;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Handles Spring framework exceptions using registered {@link
 * io.github.malczuuu.problem4j.spring.web.resolver.ProblemResolver}s.
 *
 * <p>This class extends {@link ResponseEntityExceptionHandler} and overrides {@link
 * #handleExceptionInternal} to replace the response body with a {@link Problem} object.
 */
@RestControllerAdvice
public class ProblemEnhancedMvcHandler extends ResponseEntityExceptionHandler {

  private final ProblemResolverStore problemResolverStore;
  private final ProblemPostProcessor problemPostProcessor;

  private final List<AdviceMvcInspector> adviceMvcInspectors;

  public ProblemEnhancedMvcHandler(
      ProblemResolverStore problemResolverStore,
      ProblemPostProcessor problemPostProcessor,
      List<AdviceMvcInspector> adviceMvcInspectors) {
    this.problemResolverStore = problemResolverStore;
    this.problemPostProcessor = problemPostProcessor;
    this.adviceMvcInspectors = adviceMvcInspectors;
  }

  /**
   * <b>Note:</b> Although {@link HttpHeaders#writableHttpHeaders(HttpHeaders)} is deprecated, it is
   * used here for backward compatibility with older Spring Framework versions.
   *
   * <p>The deprecation alternative provided by Spring is not available in versions {@code 6.0.*}
   * and {@code 6.1.*} (Spring Framework versions, not Spring Boot). Therefore, this method is
   * retained to ensure compatibility across those versions.
   */
  @Override
  protected ResponseEntity<Object> handleExceptionInternal(
      Exception ex, Object body, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    ProblemContext context = (ProblemContext) request.getAttribute(PROBLEM_CONTEXT, SCOPE_REQUEST);
    if (context == null) {
      context = ProblemContext.empty();
    }

    headers = headers != null ? HttpHeaders.writableHttpHeaders(headers) : new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

    Problem problem = getBuilderForOverridingBody(context, ex, headers, status).build();
    problem = problemPostProcessor.process(context, problem);

    status = resolveStatus(problem);

    for (AdviceMvcInspector inspector : adviceMvcInspectors) {
      inspector.inspect(context, problem, ex, headers, status, request);
    }

    return super.handleExceptionInternal(ex, problem, headers, status, request);
  }

  private ProblemBuilder getBuilderForOverridingBody(
      ProblemContext context, Exception ex, HttpHeaders headers, HttpStatusCode status) {
    try {
      return problemResolverStore
          .findResolver(ex.getClass())
          .map(resolver -> resolver.resolveBuilder(context, ex, headers, status))
          .orElseGet(() -> fallbackProblem(status));
    } catch (Exception e) {
      return fallbackProblem(status);
    }
  }

  private ProblemBuilder fallbackProblem(HttpStatusCode status) {
    return Problem.builder().status(resolveStatus(status));
  }
}
