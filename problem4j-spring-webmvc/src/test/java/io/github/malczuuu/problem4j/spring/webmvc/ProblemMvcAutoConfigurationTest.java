package io.github.malczuuu.problem4j.spring.webmvc;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {JacksonAutoConfiguration.class, ProblemMvcAutoConfiguration.class})
class ProblemMvcAutoConfigurationTest {

  @Test
  void contextLoads() {}
}
