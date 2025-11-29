package io.github.malczuuu.problem4j.spring.webmvc.error;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.malczuuu.problem4j.spring.webmvc.ProblemMvcProperties;
import io.github.malczuuu.problem4j.spring.webmvc.app.MvcTestApp;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.error.ErrorController;

class ProblemErrorMvcConfigurationTest {

  @SpringBootTest(classes = {MvcTestApp.class})
  @Nested
  class WithEnabled {

    @Autowired(required = false)
    private ProblemErrorMvcConfiguration problemErrorMvcConfiguration;

    @Autowired private ErrorController errorController;

    @Autowired private ProblemMvcProperties properties;

    @Test
    void contextLoads() {
      assertThat(problemErrorMvcConfiguration).isNotNull();
      assertThat(errorController).isInstanceOf(ProblemErrorController.class);
      assertThat(properties.getErrorController().isEnabled()).isTrue();
    }
  }

  @SpringBootTest(
      classes = {MvcTestApp.class},
      properties = {"problem4j.webmvc.error-controller.enabled=false"})
  @Nested
  class WithDisabled {

    @Autowired(required = false)
    private ProblemErrorMvcConfiguration problemErrorMvcConfiguration;

    @Autowired private ErrorController errorController;

    @Autowired private ProblemMvcProperties properties;

    @Test
    void contextLoads() {
      assertThat(problemErrorMvcConfiguration).isNull();
      assertThat(errorController).isNotInstanceOf(ProblemErrorController.class);
      assertThat(properties.getErrorController().isEnabled()).isFalse();
    }
  }
}
