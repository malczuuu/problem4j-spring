package io.github.malczuuu.problem4j.spring.web.mapping;

import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.ATTRIBUTE_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.COOKIE_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.COOKIE_LABEL;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.HEADER_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.HEADER_LABEL;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.KIND_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.MISSING_COOKIE_DETAIL;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.MISSING_HEADER_DETAIL;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.MISSING_PATH_VARIABLE_DETAIL;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.MISSING_REQUEST_ATTRIBUTE_DETAIL;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.MISSING_REQUEST_PARAM_DETAIL;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.MISSING_REQUEST_PART_DETAIL;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.MISSING_SESSION_ATTRIBUTE_DETAIL;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.NAME_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.PARAM_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.PATH_PARAMETER_LABEL;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.QUERY_PARAMETER_LABEL;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.REQUEST_ATTRIBUTE_LABEL;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.REQUEST_PART_LABEL;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.SESSION_ATTRIBUTE_LABEL;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.format.DetailFormat;
import java.util.Locale;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.MissingRequestValueException;

public class MissingRequestValueMapping implements ExceptionMapping {

  private final DetailFormat detailFormat;

  public MissingRequestValueMapping(DetailFormat detailFormat) {
    this.detailFormat = detailFormat;
  }

  @Override
  public Class<? extends Exception> getExceptionClass() {
    return MissingRequestValueException.class;
  }

  @Override
  public Problem map(Exception ex, HttpHeaders headers, HttpStatusCode status) {
    MissingRequestValueException e = (MissingRequestValueException) ex;

    ProblemBuilder builder = Problem.builder().status(ProblemStatus.BAD_REQUEST);

    if (e.getLabel() == null) {
      return builder.build();
    }

    switch (e.getLabel()) {
      case QUERY_PARAMETER_LABEL ->
          builder =
              builder
                  .detail(detailFormat.format(MISSING_REQUEST_PARAM_DETAIL))
                  .extension(PARAM_EXTENSION, e.getName())
                  .extension(KIND_EXTENSION, e.getType().getSimpleName().toLowerCase(Locale.ROOT));
      case REQUEST_PART_LABEL ->
          builder =
              builder
                  .detail(detailFormat.format(MISSING_REQUEST_PART_DETAIL))
                  .extension(PARAM_EXTENSION, e.getName());
      case HEADER_LABEL ->
          builder =
              builder
                  .detail(detailFormat.format(MISSING_HEADER_DETAIL))
                  .extension(HEADER_EXTENSION, e.getName());
      case PATH_PARAMETER_LABEL ->
          builder =
              builder
                  .detail(detailFormat.format(MISSING_PATH_VARIABLE_DETAIL))
                  .extension(NAME_EXTENSION, e.getName());
      case COOKIE_LABEL ->
          builder =
              builder
                  .detail(detailFormat.format(MISSING_COOKIE_DETAIL))
                  .extension(COOKIE_EXTENSION, e.getName());
      case REQUEST_ATTRIBUTE_LABEL ->
          builder =
              builder
                  .detail(detailFormat.format(MISSING_REQUEST_ATTRIBUTE_DETAIL))
                  .extension(ATTRIBUTE_EXTENSION, e.getName());
      case SESSION_ATTRIBUTE_LABEL ->
          builder =
              builder
                  .detail(detailFormat.format(MISSING_SESSION_ATTRIBUTE_DETAIL))
                  .extension(ATTRIBUTE_EXTENSION, e.getName());
    }

    return builder.build();
  }
}
