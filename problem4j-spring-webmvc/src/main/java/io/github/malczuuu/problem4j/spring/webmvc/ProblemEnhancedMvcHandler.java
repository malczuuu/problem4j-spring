package io.github.malczuuu.problem4j.spring.webmvc;

import static io.github.malczuuu.problem4j.spring.web.context.ContextSupport.PROBLEM_CONTEXT;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.resolveStatus;
import static io.github.malczuuu.problem4j.spring.webmvc.MvcAdviceSupport.logAdviceException;
import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.ProblemResolverStore;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.processor.ProblemPostProcessor;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Handles Spring framework exceptions using registered {@code ProblemResolver}s.
 *
 * <p>This class extends {@link ResponseEntityExceptionHandler} and overrides {@code
 * handleExceptionInternal} to replace the response body with a {@link Problem} object.
 *
 * @see io.github.malczuuu.problem4j.spring.web.resolver.ProblemResolver
 */
@RestControllerAdvice
public class ProblemEnhancedMvcHandler extends ResponseEntityExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(ProblemEnhancedMvcHandler.class);

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

  @Override
  protected ResponseEntity<Object> handleExceptionInternal(
      Exception ex, Object body, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    ProblemContext context = (ProblemContext) request.getAttribute(PROBLEM_CONTEXT, SCOPE_REQUEST);
    if (context == null) {
      context = ProblemContext.empty();
    }

    headers = headers != null ? new HttpHeaders(headers) : new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

    Problem problem;
    try {
      problem = getBuilderForOverridingBody(context, ex, headers, status).build();
      problem = problemPostProcessor.process(context, problem);
    } catch (Exception e) {
      logAdviceException(log, ex, request, e);
      problem = Problem.builder().status(ProblemStatus.INTERNAL_SERVER_ERROR).build();
    }

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
