package io.github.malczuuu.problem4j.spring.web.mapping;

import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.ERRORS_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.VALIDATION_FAILED_DETAIL;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.format.ProblemFormat;
import io.github.malczuuu.problem4j.spring.web.model.Violation;
import java.util.ArrayList;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.support.WebExchangeBindException;

public class WebExchangeBindMapping extends AbstractExceptionMapping {

  public WebExchangeBindMapping(ProblemFormat problemFormat) {
    super(WebExchangeBindException.class, problemFormat);
  }

  @Override
  public Problem map(Exception ex, HttpHeaders headers, HttpStatusCode status) {
    WebExchangeBindException e = (WebExchangeBindException) ex;
    return from(e.getBindingResult()).status(ProblemStatus.BAD_REQUEST).build();
  }

  private ProblemBuilder from(BindingResult bindingResult) {
    ArrayList<Violation> details = new ArrayList<>();
    bindingResult
        .getFieldErrors()
        .forEach(f -> details.add(new Violation(f.getField(), f.getDefaultMessage())));
    bindingResult
        .getGlobalErrors()
        .forEach(g -> details.add(new Violation(null, g.getDefaultMessage())));
    return Problem.builder()
        .detail(formatDetail(VALIDATION_FAILED_DETAIL))
        .extension(ERRORS_EXTENSION, details);
  }
}
