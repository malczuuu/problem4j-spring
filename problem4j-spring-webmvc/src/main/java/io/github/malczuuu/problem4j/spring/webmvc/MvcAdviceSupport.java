package io.github.malczuuu.problem4j.spring.webmvc;

import static io.github.malczuuu.problem4j.spring.web.context.ContextSupport.TRACE_ID;
import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

import org.slf4j.Logger;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

class MvcAdviceSupport {

  static void logAdviceException(Logger log, Exception ex, WebRequest request, Exception e) {
    if (request instanceof ServletWebRequest req) {
      if (req.getRequest().getRequestURI() != null) {
        log.warn(
            "Unable to resolve problem response (method={}, endpoint={}, traceId={}, message={}, originalException=[{} : {}])",
            req.getHttpMethod(),
            req.getRequest().getRequestURI(),
            req.getAttribute(TRACE_ID, SCOPE_REQUEST),
            e.getMessage(),
            ex.getClass().getName(),
            ex.getMessage(),
            e);
      }
    }
  }
}
