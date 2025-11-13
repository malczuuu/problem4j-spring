package io.github.malczuuu.problem4j.spring.web;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.malczuuu.problem4j.spring.web.app.TestApp;
import io.github.malczuuu.problem4j.spring.web.resolver.ProblemResolverConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    classes = {TestApp.class},
    properties = {"problem4j.enabled=false"})
class ProblemAutoConfigurationDisabledTest {

  @Autowired(required = false)
  private ProblemAutoConfiguration problemAutoConfiguration;

  @Autowired(required = false)
  private ProblemResolverConfiguration problemResolverConfiguration;

  @Test
  void contextLoadsWithoutProblemConfiguration() {
    assertThat(problemAutoConfiguration).isNull();
    assertThat(problemResolverConfiguration).isNull();
  }
}
