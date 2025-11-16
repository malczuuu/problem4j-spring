package io.github.malczuuu.problem4j.spring.webmvc;

import static io.github.malczuuu.problem4j.spring.web.context.ContextSupport.TRACE_ID;
import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

import org.slf4j.Logger;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

class MvcAdviceSupport {

  static void logAdviceException(Logger log, Exception ex, WebRequest request, Exception e) {
    String method = "<unknown>";
    String endpoint = "<unknown>";
    String traceId = null;

    if (request instanceof ServletWebRequest req && req.getRequest().getRequestURI() != null) {
      method = String.valueOf(req.getHttpMethod());
      endpoint = req.getRequest().getRequestURI();

      Object traceIdAttr = req.getAttribute(TRACE_ID, SCOPE_REQUEST);
      if (traceIdAttr != null) {
        traceId = traceIdAttr.toString();
      }
    }

    log.warn(
        "Unable to resolve problem response (method={}, endpoint={}, traceId={}, message={}, originalException=[{} : {}])",
        method,
        endpoint,
        traceId,
        e.getMessage(),
        ex.getClass().getName(),
        ex.getMessage(),
        e);
  }
}
