package io.github.malczuuu.problem4j.spring.web.resolver;

import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.resolveStatus;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.format.IdentityProblemFormat;
import io.github.malczuuu.problem4j.spring.web.util.ProblemSupport;
import java.lang.reflect.Method;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ServerWebInputException;

class ServerWebInputResolverTest {

  private ServerWebInputResolver serverWebInputMapping;

  @BeforeEach
  void beforeEach() {
    serverWebInputMapping = new ServerWebInputResolver(new IdentityProblemFormat());
  }

  @Test
  void givenExceptionWithCauseAndWithoutPropertyName_shouldDelegateAndIncludeMethodParameter()
      throws NoSuchMethodException {
    Method method = DummyController.class.getMethod("paramMethod", Boolean.class);
    MethodParameter parameter = new MethodParameter(method, 0);
    parameter.initParameterNameDiscovery(new DefaultParameterNameDiscoverer());

    TypeMismatchException cause = new TypeMismatchException("42", Boolean.class);

    ServerWebInputException ex = new ServerWebInputException("irrelevant reason", parameter, cause);

    Problem problem =
        serverWebInputMapping.resolveProblem(
            ProblemContext.ofTraceId("traceId"),
            ex,
            new HttpHeaders(),
            HttpStatusCode.valueOf(400));

    assertThat(problem)
        .isEqualTo(
            Problem.builder()
                .status(ProblemStatus.BAD_REQUEST)
                .detail(ProblemSupport.TYPE_MISMATCH_DETAIL)
                .extension(ProblemSupport.PROPERTY_EXTENSION, "value")
                .extension(ProblemSupport.KIND_EXTENSION, "boolean")
                .build());
  }

  @Test
  void givenExceptionWithCauseAndWithoutParameter_shouldDelegateToMethodParameter() {
    TypeMismatchException cause = new TypeMismatchException("42", Boolean.class);
    cause.initPropertyName("flag");

    ServerWebInputException ex = new ServerWebInputException("irrelevant reason", null, cause);

    Problem problem =
        serverWebInputMapping.resolveProblem(
            ProblemContext.ofTraceId("traceId"),
            ex,
            new HttpHeaders(),
            HttpStatusCode.valueOf(400));

    assertThat(problem)
        .isEqualTo(
            Problem.builder()
                .status(ProblemStatus.BAD_REQUEST)
                .detail(ProblemSupport.TYPE_MISMATCH_DETAIL)
                .extension(ProblemSupport.PROPERTY_EXTENSION, "flag")
                .extension(ProblemSupport.KIND_EXTENSION, "boolean")
                .build());
  }

  @Test
  void givenExceptionWithoutCause_shouldReturnSimpleProblem() {
    ServerWebInputException ex = new ServerWebInputException("irrelevant reason");

    Problem problem =
        serverWebInputMapping.resolveProblem(ProblemContext.ofTraceId("traceId"), ex, null, null);

    assertThat(problem)
        .isEqualTo(Problem.builder().status(resolveStatus(ex.getStatusCode())).build());
  }

  static class DummyController {
    public void paramMethod(Boolean value) {}
  }
}
