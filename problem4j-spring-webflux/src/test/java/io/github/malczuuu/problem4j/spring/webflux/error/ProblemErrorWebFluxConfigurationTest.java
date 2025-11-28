package io.github.malczuuu.problem4j.spring.webflux.error;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.malczuuu.problem4j.spring.webflux.ProblemWebFluxProperties;
import io.github.malczuuu.problem4j.spring.webflux.app.WebFluxTestApp;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;

class ProblemErrorWebFluxConfigurationTest {

  @SpringBootTest(classes = {WebFluxTestApp.class})
  @Nested
  class WithEnabled {

    @Autowired(required = false)
    private ProblemErrorWebFluxConfiguration problemErrorWebFluxConfiguration;

    @Autowired private ErrorWebExceptionHandler errorWebExceptionHandler;

    @Autowired private ProblemWebFluxProperties properties;

    @Test
    void contextLoads() {
      assertThat(problemErrorWebFluxConfiguration).isNotNull();
      assertThat(errorWebExceptionHandler).isInstanceOf(ProblemErrorWebExceptionHandler.class);
      assertThat(properties.getErrorWebExceptionHandler().isEnabled()).isTrue();
    }
  }

  @SpringBootTest(
      classes = {WebFluxTestApp.class},
      properties = {"problem4j.webflux.error-web-exception-handler.enabled=false"})
  @Nested
  class WithDisabled {

    @Autowired(required = false)
    private ProblemErrorWebFluxConfiguration problemErrorWebFluxConfiguration;

    @Autowired private ErrorWebExceptionHandler errorWebExceptionHandler;

    @Autowired private ProblemWebFluxProperties properties;

    @Test
    void contextLoads() {
      assertThat(problemErrorWebFluxConfiguration).isNull();
      assertThat(errorWebExceptionHandler).isNotInstanceOf(ProblemErrorWebExceptionHandler.class);
      assertThat(properties.getErrorWebExceptionHandler().isEnabled()).isFalse();
    }
  }
}
