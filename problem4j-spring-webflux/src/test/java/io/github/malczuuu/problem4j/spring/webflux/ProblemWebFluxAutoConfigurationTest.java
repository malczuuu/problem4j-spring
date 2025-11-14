package io.github.malczuuu.problem4j.spring.webflux;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.malczuuu.problem4j.spring.webflux.app.WebFluxTestApp;
import io.github.malczuuu.problem4j.spring.webflux.context.ProblemContextWebFluxFilter;
import io.github.malczuuu.problem4j.spring.webflux.error.ProblemErrorWebFluxConfiguration;
import io.github.malczuuu.problem4j.spring.webflux.resolver.ProblemResolverWebFluxConfiguration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

class ProblemWebFluxAutoConfigurationTest {
  @SpringBootTest(classes = {WebFluxTestApp.class})
  @Nested
  class WithEnabled {

    @Autowired(required = false)
    private ProblemWebFluxAutoConfiguration problemWebFluxAutoConfiguration;

    @Autowired(required = false)
    private ProblemErrorWebFluxConfiguration problemErrorWebFluxConfiguration;

    @Autowired(required = false)
    private ProblemResolverWebFluxConfiguration problemResolverWebFluxConfiguration;

    @Autowired(required = false)
    private ProblemWebFluxProperties properties;

    @Test
    void contextLoads() {
      assertThat(problemWebFluxAutoConfiguration).isNotNull();
      assertThat(problemErrorWebFluxConfiguration).isNotNull();
      assertThat(problemResolverWebFluxConfiguration).isNotNull();

      assertThat(properties).isNotNull();
      assertThat(properties.isEnabled()).isTrue();
    }
  }

  @SpringBootTest(
      classes = {WebFluxTestApp.class},
      properties = {"problem4j.webflux.enabled=false"})
  @Nested
  class WithDisabled {

    @Autowired(required = false)
    private ProblemWebFluxAutoConfiguration problemWebFluxAutoConfiguration;

    @Autowired(required = false)
    private ProblemErrorWebFluxConfiguration problemErrorWebFluxConfiguration;

    @Autowired(required = false)
    private ProblemResolverWebFluxConfiguration problemResolverWebFluxConfiguration;

    @Autowired(required = false)
    private ProblemWebFluxProperties properties;

    @Test
    void contextLoadsWithoutProblemConfiguration() {
      assertThat(problemWebFluxAutoConfiguration).isNull();
      assertThat(problemErrorWebFluxConfiguration).isNull();
      assertThat(problemResolverWebFluxConfiguration).isNull();

      assertThat(properties).isNull();
    }
  }

  @SpringBootTest(classes = {WebFluxTestApp.class})
  @Nested
  class WithExceptionAdviceEnabled {

    @Autowired(required = false)
    private ExceptionWebFluxAdvice exceptionWebFluxAdvice;

    @Autowired(required = false)
    private ProblemWebFluxProperties properties;

    @Test
    void contextLoadsWithoutExceptionAdvice() {
      assertThat(exceptionWebFluxAdvice).isNotNull();
      assertThat(properties.getExceptionAdvice().isEnabled()).isTrue();
    }
  }

  @SpringBootTest(
      classes = {WebFluxTestApp.class},
      properties = {"problem4j.webflux.exception-advice.enabled=false"})
  @Nested
  class WithExceptionAdviceDisabled {

    @Autowired(required = false)
    private ExceptionWebFluxAdvice exceptionWebFluxAdvice;

    @Autowired(required = false)
    private ProblemWebFluxProperties properties;

    @Test
    void contextLoadsWithoutExceptionAdvice() {
      assertThat(exceptionWebFluxAdvice).isNull();
      assertThat(properties.getExceptionAdvice().isEnabled()).isFalse();
    }
  }

  @SpringBootTest(classes = {WebFluxTestApp.class})
  @Nested
  class WithProblemExceptionAdviceEnabled {

    @Autowired(required = false)
    private ProblemExceptionWebFluxAdvice problemExceptionWebFluxAdvice;

    @Autowired(required = false)
    private ProblemWebFluxProperties properties;

    @Test
    void contextLoadsWithoutExceptionAdvice() {
      assertThat(problemExceptionWebFluxAdvice).isNotNull();
      assertThat(properties.getProblemExceptionAdvice().isEnabled()).isTrue();
    }
  }

  @SpringBootTest(
      classes = {WebFluxTestApp.class},
      properties = {"problem4j.webflux.problem-exception-advice.enabled=false"})
  @Nested
  class WithProblemExceptionAdviceDisabled {

    @Autowired(required = false)
    private ProblemExceptionWebFluxAdvice problemExceptionWebFluxAdvice;

    @Autowired(required = false)
    private ProblemWebFluxProperties properties;

    @Test
    void contextLoadsWithoutExceptionAdvice() {
      assertThat(problemExceptionWebFluxAdvice).isNull();
      assertThat(properties.getProblemExceptionAdvice().isEnabled()).isFalse();
    }
  }

  @SpringBootTest(classes = {WebFluxTestApp.class})
  @Nested
  class WithProblemContextFilterEnabled {

    @Autowired(required = false)
    private ProblemContextWebFluxFilter problemContextWebFluxFilter;

    @Autowired(required = false)
    private ProblemWebFluxProperties properties;

    @Test
    void contextLoadsWithoutExceptionAdvice() {
      assertThat(problemContextWebFluxFilter).isNotNull();
      assertThat(properties.getProblemContextFilter().isEnabled()).isTrue();
    }
  }

  @SpringBootTest(
      classes = {WebFluxTestApp.class},
      properties = {"problem4j.webflux.problem-context-filter.enabled=false"})
  @Nested
  class WithProblemContextFilterDisabled {

    @Autowired(required = false)
    private ProblemContextWebFluxFilter problemContextWebFluxFilter;

    @Autowired(required = false)
    private ProblemWebFluxProperties properties;

    @Test
    void contextLoadsWithoutExceptionAdvice() {
      assertThat(problemContextWebFluxFilter).isNull();
      assertThat(properties.getProblemContextFilter().isEnabled()).isFalse();
    }
  }

  @SpringBootTest(classes = {WebFluxTestApp.class})
  @Nested
  class WithExceptionHandlerEnabled {

    @Autowired(required = false)
    private ResponseEntityExceptionHandler exceptionHandler;

    @Autowired(required = false)
    private ProblemWebFluxProperties properties;

    @Test
    void contextLoadsWithoutExceptionAdvice() {
      assertThat(exceptionHandler).isNotNull();
      assertThat(properties.getExceptionHandler().isEnabled()).isTrue();
    }
  }

  @SpringBootTest(
      classes = {WebFluxTestApp.class},
      properties = {"problem4j.webflux.exception-handler.enabled=false"})
  @Nested
  class WithExceptionHandlerDisabled {

    @Autowired(required = false)
    private ResponseEntityExceptionHandler exceptionHandler;

    @Autowired(required = false)
    private ProblemWebFluxProperties properties;

    @Test
    void contextLoadsWithoutExceptionAdvice() {
      assertThat(exceptionHandler).isNull();
      assertThat(properties.getExceptionHandler().isEnabled()).isFalse();
    }
  }
}
