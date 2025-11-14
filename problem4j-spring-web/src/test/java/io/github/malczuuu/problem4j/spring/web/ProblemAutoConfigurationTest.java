package io.github.malczuuu.problem4j.spring.web;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.malczuuu.problem4j.spring.web.app.TestApp;
import io.github.malczuuu.problem4j.spring.web.resolver.ProblemResolverConfiguration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {TestApp.class})
class ProblemAutoConfigurationTest {

  @SpringBootTest(classes = {TestApp.class})
  @Nested
  class WithEnabled {

    @Autowired(required = false)
    private ProblemAutoConfiguration problemAutoConfiguration;

    @Autowired(required = false)
    private ProblemResolverConfiguration problemResolverConfiguration;

    @Autowired(required = false)
    private ProblemProperties properties;

    @Test
    void contextLoads() {
      assertThat(problemAutoConfiguration).isNotNull();
      assertThat(problemResolverConfiguration).isNotNull();

      assertThat(properties).isNotNull();
      assertThat(properties.isEnabled()).isTrue();
    }
  }

  @SpringBootTest(
      classes = {TestApp.class},
      properties = {"problem4j.enabled=false"})
  @Nested
  class WithDisabled {

    @Autowired(required = false)
    private ProblemAutoConfiguration problemAutoConfiguration;

    @Autowired(required = false)
    private ProblemResolverConfiguration problemResolverConfiguration;

    @Autowired(required = false)
    private ProblemProperties properties;

    @Test
    void contextLoadsWithoutProblemConfiguration() {
      assertThat(problemAutoConfiguration).isNull();
      assertThat(problemResolverConfiguration).isNull();

      assertThat(properties).isNull();
    }
  }
}
