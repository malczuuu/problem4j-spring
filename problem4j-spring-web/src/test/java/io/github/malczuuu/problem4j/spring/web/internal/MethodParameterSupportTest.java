package io.github.malczuuu.problem4j.spring.web.internal;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

class MethodParameterSupportTest {

  @Test
  void givenPathVariable_whenFindParameterName_thenReturnsName() throws NoSuchMethodException {
    Method method = TestController.class.getMethod("pathVariableMethod", String.class);
    MethodParameter parameter = new MethodParameter(method, 0);

    Optional<String> name = MethodParameterSupport.findParameterName(parameter);

    assertThat(name).contains("id");
  }

  @Test
  void givenRequestParam_whenFindParameterName_thenReturnsName() throws NoSuchMethodException {
    Method method = TestController.class.getMethod("requestParamMethod", String.class);
    MethodParameter parameter = new MethodParameter(method, 0);

    Optional<String> name = MethodParameterSupport.findParameterName(parameter);

    assertThat(name).contains("param");
  }

  @Test
  void givenRequestHeader_whenFindParameterName_thenReturnsName() throws NoSuchMethodException {
    Method method = TestController.class.getMethod("requestHeaderMethod", String.class);
    MethodParameter parameter = new MethodParameter(method, 0);

    Optional<String> name = MethodParameterSupport.findParameterName(parameter);

    assertThat(name).contains("X-Custom-Header");
  }

  @Test
  void givenCookieValue_whenFindParameterName_thenReturnsName() throws NoSuchMethodException {
    Method method = TestController.class.getMethod("cookieValueMethod", String.class);
    MethodParameter parameter = new MethodParameter(method, 0);

    Optional<String> name = MethodParameterSupport.findParameterName(parameter);

    assertThat(name).contains("SESSIONID");
  }

  @Test
  void givenSessionAttribute_whenFindParameterName_thenReturnsName() throws NoSuchMethodException {
    Method method = TestController.class.getMethod("sessionAttributeMethod", String.class);
    MethodParameter parameter = new MethodParameter(method, 0);

    Optional<String> name = MethodParameterSupport.findParameterName(parameter);

    assertThat(name).contains("user");
  }

  @Test
  void givenRequestAttribute_whenFindParameterName_thenReturnsName() throws NoSuchMethodException {
    Method method = TestController.class.getMethod("requestAttributeMethod", String.class);
    MethodParameter parameter = new MethodParameter(method, 0);

    Optional<String> name = MethodParameterSupport.findParameterName(parameter);

    assertThat(name).contains("attr");
  }

  @Test
  void givenMatrixVariable_whenFindParameterName_thenReturnsName() throws NoSuchMethodException {
    Method method = TestController.class.getMethod("matrixVariableMethod", String.class);
    MethodParameter parameter = new MethodParameter(method, 0);

    Optional<String> name = MethodParameterSupport.findParameterName(parameter);

    assertThat(name).contains("matrix");
  }

  @Test
  void givenNoAnnotation_whenFindParameterName_thenReturnsName() throws NoSuchMethodException {
    Method method = TestController.class.getMethod("noAnnotationMethod", String.class);
    MethodParameter parameter = new MethodParameter(method, 0);
    parameter.initParameterNameDiscovery(new DefaultParameterNameDiscoverer());

    Optional<String> name = MethodParameterSupport.findParameterName(parameter);

    assertThat(name).contains("value");
  }

  static class TestController {

    public void pathVariableMethod(@PathVariable("id") String id) {}

    public void requestParamMethod(@RequestParam("param") String param) {}

    public void requestHeaderMethod(@RequestHeader("X-Custom-Header") String header) {}

    public void cookieValueMethod(@CookieValue("SESSIONID") String cookie) {}

    public void sessionAttributeMethod(@SessionAttribute("user") String user) {}

    public void requestAttributeMethod(@RequestAttribute("attr") String attr) {}

    public void matrixVariableMethod(@MatrixVariable("matrix") String matrix) {}

    public void noAnnotationMethod(String value) {}
  }
}
