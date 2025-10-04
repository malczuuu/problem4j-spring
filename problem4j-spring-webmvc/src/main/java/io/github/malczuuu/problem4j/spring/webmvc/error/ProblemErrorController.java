package io.github.malczuuu.problem4j.spring.webmvc.error;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.spring.web.util.TracingSupport;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * A simple {@link org.springframework.boot.web.servlet.error.ErrorController} implementation that
 * returns HTTP problems (RFC 7807) instead of HTML error pages.
 *
 * <p>It converts generic servlet errors into {@link Problem} responses with the appropriate HTTP
 * status and content type {@code application/problem+json}.
 */
@Controller
@RequestMapping("${server.error.path:${error.path:/error}}")
public class ProblemErrorController extends AbstractErrorController {
  /**
   * Creates a new {@code ProblemErrorController}.
   *
   * @param errorAttributes the error attributes used to obtain error information
   */
  public ProblemErrorController(ErrorAttributes errorAttributes) {
    super(errorAttributes);
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

    ProblemBuilder builder = Problem.builder().status(status.value());

    Object optionalInstanceOverride = request.getAttribute(TracingSupport.INSTANCE_OVERRIDE_ATTR);
    if (optionalInstanceOverride != null) {
      builder = builder.instance(optionalInstanceOverride.toString());
    }

    Problem problem = builder.build();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

    return new ResponseEntity<>(problem, headers, status);
  }
}
