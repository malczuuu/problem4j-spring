package io.github.malczuuu.problem4j.spring.web.mapping;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.format.DetailFormat;
import io.github.malczuuu.problem4j.spring.web.format.PropertyNameFormat;
import io.github.malczuuu.problem4j.spring.web.model.Violation;
import java.util.ArrayList;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.support.WebExchangeBindException;

public class WebExchangeBindMapping implements ExceptionMapping {

  private final DetailFormat detailFormat;
  private final PropertyNameFormat propertyNameFormat;

  public WebExchangeBindMapping(DetailFormat detailFormat, PropertyNameFormat propertyNameFormat) {
    this.detailFormat = detailFormat;
    this.propertyNameFormat = propertyNameFormat;
  }

  @Override
  public Class<WebExchangeBindException> getExceptionClass() {
    return WebExchangeBindException.class;
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
        .forEach(
            f ->
                details.add(
                    new Violation(propertyNameFormat.format(f.getField()), f.getDefaultMessage())));
    bindingResult
        .getGlobalErrors()
        .forEach(g -> details.add(new Violation(null, g.getDefaultMessage())));
    return Problem.builder()
        .detail(detailFormat.format("Validation failed"))
        .extension("errors", details);
  }
}
