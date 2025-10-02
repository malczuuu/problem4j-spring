package io.github.malczuuu.problem4j.spring.webflux;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {JacksonAutoConfiguration.class, ProblemFluxAutoConfiguration.class})
class ProblemFluxAutoConfigurationTest {

  @Test
  void contextLoads() {}
}
