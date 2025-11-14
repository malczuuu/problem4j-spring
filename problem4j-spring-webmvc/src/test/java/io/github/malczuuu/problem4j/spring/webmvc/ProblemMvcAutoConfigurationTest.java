package io.github.malczuuu.problem4j.spring.webmvc;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.malczuuu.problem4j.spring.webmvc.app.MvcTestApp;
import io.github.malczuuu.problem4j.spring.webmvc.context.ProblemContextMvcFilter;
import io.github.malczuuu.problem4j.spring.webmvc.error.ProblemErrorMvcConfiguration;
import io.github.malczuuu.problem4j.spring.webmvc.resolver.ProblemResolverMvcConfiguration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

class ProblemMvcAutoConfigurationTest {

  @SpringBootTest(classes = {MvcTestApp.class})
  @Nested
  class WithEnabled {

    @Autowired(required = false)
    private ProblemMvcAutoConfiguration problemMvcAutoConfiguration;

    @Autowired(required = false)
    private ProblemErrorMvcConfiguration problemErrorMvcConfiguration;

    @Autowired(required = false)
    private ProblemResolverMvcConfiguration problemResolverMvcConfiguration;

    @Autowired(required = false)
    private ProblemMvcProperties properties;

    @Test
    void contextLoads() {
      assertThat(problemMvcAutoConfiguration).isNotNull();
      assertThat(problemErrorMvcConfiguration).isNotNull();
      assertThat(problemResolverMvcConfiguration).isNotNull();

      assertThat(properties).isNotNull();
      assertThat(properties.isEnabled()).isTrue();
    }
  }

  @SpringBootTest(
      classes = {MvcTestApp.class},
      properties = {"problem4j.webmvc.enabled=false"})
  @Nested
  class WithDisabled {

    @Autowired(required = false)
    private ProblemMvcAutoConfiguration problemMvcAutoConfiguration;

    @Autowired(required = false)
    private ProblemErrorMvcConfiguration problemErrorMvcConfiguration;

    @Autowired(required = false)
    private ProblemResolverMvcConfiguration problemResolverMvcConfiguration;

    @Autowired(required = false)
    private ProblemMvcProperties properties;

    @Test
    void contextLoadsWithoutConfiguration() {
      assertThat(problemMvcAutoConfiguration).isNull();
      assertThat(problemErrorMvcConfiguration).isNull();
      assertThat(problemResolverMvcConfiguration).isNull();

      assertThat(properties).isNull();
    }
  }

  @SpringBootTest(classes = {MvcTestApp.class})
  @Nested
  class WithExceptionAdviceEnabled {

    @Autowired(required = false)
    private ExceptionMvcAdvice exceptionMvcAdvice;

    @Autowired(required = false)
    private ProblemMvcProperties properties;

    @Test
    void contextLoadsWithoutExceptionAdvice() {
      assertThat(exceptionMvcAdvice).isNotNull();
      assertThat(properties.getExceptionAdvice().isEnabled()).isTrue();
    }
  }

  @SpringBootTest(
      classes = {MvcTestApp.class},
      properties = {"problem4j.webmvc.exception-advice.enabled=false"})
  @Nested
  class WithExceptionAdviceDisabled {

    @Autowired(required = false)
    private ExceptionMvcAdvice exceptionMvcAdvice;

    @Autowired(required = false)
    private ProblemMvcProperties properties;

    @Test
    void contextLoadsWithoutExceptionAdvice() {
      assertThat(exceptionMvcAdvice).isNull();
      assertThat(properties.getExceptionAdvice().isEnabled()).isFalse();
    }
  }

  @SpringBootTest(classes = {MvcTestApp.class})
  @Nested
  class WithProblemExceptionAdviceEnabled {

    @Autowired(required = false)
    private ProblemExceptionMvcAdvice problemExceptionMvcAdvice;

    @Autowired(required = false)
    private ProblemMvcProperties properties;

    @Test
    void contextLoadsWithoutExceptionAdvice() {
      assertThat(problemExceptionMvcAdvice).isNotNull();
      assertThat(properties.getProblemExceptionAdvice().isEnabled()).isTrue();
    }
  }

  @SpringBootTest(
      classes = {MvcTestApp.class},
      properties = {"problem4j.webmvc.problem-exception-advice.enabled=false"})
  @Nested
  class WithProblemExceptionAdviceDisabled {

    @Autowired(required = false)
    private ProblemExceptionMvcAdvice problemExceptionMvcAdvice;

    @Autowired(required = false)
    private ProblemMvcProperties properties;

    @Test
    void contextLoadsWithoutExceptionAdvice() {
      assertThat(problemExceptionMvcAdvice).isNull();
      assertThat(properties.getProblemExceptionAdvice().isEnabled()).isFalse();
    }
  }

  @SpringBootTest(classes = {MvcTestApp.class})
  @Nested
  class WithProblemContextFilterEnabled {

    @Autowired(required = false)
    private ProblemContextMvcFilter problemContextMvcFilter;

    @Autowired(required = false)
    private ProblemMvcProperties properties;

    @Test
    void contextLoadsWithoutExceptionAdvice() {
      assertThat(problemContextMvcFilter).isNotNull();
      assertThat(properties.getProblemContextFilter().isEnabled()).isTrue();
    }
  }

  @SpringBootTest(
      classes = {MvcTestApp.class},
      properties = {"problem4j.webmvc.problem-context-filter.enabled=false"})
  @Nested
  class WithProblemContextFilterDisabled {

    @Autowired(required = false)
    private ProblemContextMvcFilter problemContextMvcFilter;

    @Autowired(required = false)
    private ProblemMvcProperties properties;

    @Test
    void contextLoadsWithoutExceptionAdvice() {
      assertThat(problemContextMvcFilter).isNull();
      assertThat(properties.getProblemContextFilter().isEnabled()).isFalse();
    }
  }

  @SpringBootTest(classes = {MvcTestApp.class})
  @Nested
  class WithExceptionHandlerEnabled {

    @Autowired(required = false)
    private ResponseEntityExceptionHandler exceptionHandler;

    @Autowired(required = false)
    private ProblemMvcProperties properties;

    @Test
    void contextLoadsWithoutExceptionAdvice() {
      assertThat(exceptionHandler).isNotNull();
      assertThat(properties.getExceptionHandler().isEnabled()).isTrue();
    }
  }

  @SpringBootTest(
      classes = {MvcTestApp.class},
      properties = {"problem4j.webmvc.exception-handler.enabled=false"})
  @Nested
  class WithExceptionHandlerDisabled {

    @Autowired(required = false)
    private ResponseEntityExceptionHandler exceptionHandler;

    @Autowired(required = false)
    private ProblemMvcProperties properties;

    @Test
    void contextLoadsWithoutExceptionAdvice() {
      assertThat(exceptionHandler).isNull();
      assertThat(properties.getExceptionHandler().isEnabled()).isFalse();
    }
  }
}
