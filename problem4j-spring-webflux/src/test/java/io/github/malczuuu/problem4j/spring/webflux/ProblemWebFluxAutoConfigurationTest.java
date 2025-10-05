package io.github.malczuuu.problem4j.spring.webflux;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    classes = {
      JacksonAutoConfiguration.class,
      ProblemWebFluxAutoConfiguration.class,
      WebFluxAutoConfiguration.class,
      ErrorWebFluxAutoConfiguration.class
    })
class ProblemWebFluxAutoConfigurationTest {

  @Test
  void contextLoads() {}
}
