package io.github.malczuuu.problem4j.spring.webflux.error;

import static org.springframework.web.reactive.function.server.RequestPredicates.all;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.util.TracingSupport;
import java.util.Optional;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public class ProblemErrorWebExceptionHandler extends DefaultErrorWebExceptionHandler {

  public ProblemErrorWebExceptionHandler(
      ErrorAttributes errorAttributes,
      WebProperties.Resources resources,
      ErrorProperties errorProperties,
      ApplicationContext applicationContext) {
    super(errorAttributes, resources, errorProperties, applicationContext);
  }

  @Override
  protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
    return route(all(), this::renderErrorResponse);
  }

  @Override
  protected Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
    return super.renderErrorResponse(request).flatMap(res -> override(request, res));
  }

  private Mono<ServerResponse> override(ServerRequest request, ServerResponse response) {
    ProblemBuilder builder =
        Problem.builder()
            .status(
                ProblemStatus.findValue(response.statusCode().value())
                    .orElse(ProblemStatus.INTERNAL_SERVER_ERROR));

    Optional<Object> optionalInstanceOverride =
        request.attribute(TracingSupport.INSTANCE_OVERRIDE_ATTR);

    if (optionalInstanceOverride.isPresent()) {
      builder = builder.instance(optionalInstanceOverride.get().toString());
    }

    Problem problem = builder.build();
    return ServerResponse.status(problem.getStatus())
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(BodyInserters.fromValue(problem));
  }
}
