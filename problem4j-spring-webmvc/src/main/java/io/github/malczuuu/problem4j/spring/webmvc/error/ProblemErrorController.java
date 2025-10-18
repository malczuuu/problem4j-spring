package io.github.malczuuu.problem4j.spring.webmvc.error;

import static io.github.malczuuu.problem4j.spring.web.context.ContextSupport.PROBLEM_CONTEXT;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.processor.ProblemPostProcessor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.webmvc.autoconfigure.error.AbstractErrorController;
import org.springframework.boot.webmvc.error.ErrorAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * A simple {@link org.springframework.boot.webmvc.error.ErrorController} implementation that
 * returns HTTP problems (RFC 7807) instead of HTML error pages.
 *
 * <p>It converts generic servlet errors into {@link Problem} responses with the appropriate HTTP
 * status and content type {@code application/problem+json}.
 */
@Controller
@RequestMapping("${server.error.path:${error.path:/error}}")
public class ProblemErrorController extends AbstractErrorController {

  private final ProblemPostProcessor problemPostProcessor;

  /**
   * Creates a new {@code ProblemErrorController}.
   *
   * @param errorAttributes the error attributes used to obtain error information
   */
  public ProblemErrorController(
      ProblemPostProcessor problemPostProcessor, ErrorAttributes errorAttributes) {
    super(errorAttributes);
    this.problemPostProcessor = problemPostProcessor;
  }

  /**
   * Handles all requests to the error path and converts them into {@link Problem} responses.
   *
   * @param request the current HTTP request
   * @return a {@link ResponseEntity} containing a {@link Problem} body and proper HTTP status
   */
  @RequestMapping
  public ResponseEntity<Problem> error(HttpServletRequest request) {
    HttpStatus status = getStatus(request);

    if (status == HttpStatus.NO_CONTENT) {
      return ResponseEntity.noContent().build();
    }

    ProblemContext context = (ProblemContext) request.getAttribute(PROBLEM_CONTEXT);
    if (context == null) {
      context = ProblemContext.empty();
    }

    Problem problem = Problem.builder().status(status.value()).build();
    problem = problemPostProcessor.process(context, problem);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

    return new ResponseEntity<>(problem, headers, status);
  }
}
