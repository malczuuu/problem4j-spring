package io.github.malczuuu.problem4j.spring.webmvc;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.malczuuu.problem4j.spring.webmvc.app.MvcTestApp;
import io.github.malczuuu.problem4j.spring.webmvc.error.ProblemErrorMvcConfiguration;
import io.github.malczuuu.problem4j.spring.webmvc.resolver.ProblemResolverMvcConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    classes = {MvcTestApp.class},
    properties = {"problem4j.webmvc.enabled=false"})
class ProblemMvcAutoConfigurationDisabledTest {

  @Autowired(required = false)
  private ProblemMvcAutoConfiguration problemMvcAutoConfiguration;

  @Autowired(required = false)
  private ProblemErrorMvcConfiguration problemErrorMvcConfiguration;

  @Autowired(required = false)
  private ProblemResolverMvcConfiguration problemResolverMvcConfiguration;

  @Test
  void contextLoadsWithoutProblemConfiguration() {
    assertThat(problemMvcAutoConfiguration).isNull();
    assertThat(problemErrorMvcConfiguration).isNull();
    assertThat(problemResolverMvcConfiguration).isNull();
  }
}
