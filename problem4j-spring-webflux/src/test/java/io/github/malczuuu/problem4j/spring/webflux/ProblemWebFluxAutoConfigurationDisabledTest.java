package io.github.malczuuu.problem4j.spring.webflux;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.malczuuu.problem4j.spring.webflux.app.WebFluxTestApp;
import io.github.malczuuu.problem4j.spring.webflux.error.ProblemErrorWebFluxConfiguration;
import io.github.malczuuu.problem4j.spring.webflux.resolver.ProblemResolverWebFluxConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    classes = {WebFluxTestApp.class},
    properties = {"problem4j.webflux.enabled=false"})
class ProblemWebFluxAutoConfigurationDisabledTest {

  @Autowired(required = false)
  private ProblemWebFluxAutoConfiguration problemWebFluxAutoConfiguration;

  @Autowired(required = false)
  private ProblemErrorWebFluxConfiguration problemErrorWebFluxConfiguration;

  @Autowired(required = false)
  private ProblemResolverWebFluxConfiguration problemResolverWebFluxConfiguration;

  @Test
  void contextLoadsWithoutProblemConfiguration() {
    assertThat(problemWebFluxAutoConfiguration).isNull();
    assertThat(problemErrorWebFluxConfiguration).isNull();
    assertThat(problemResolverWebFluxConfiguration).isNull();
  }
}
