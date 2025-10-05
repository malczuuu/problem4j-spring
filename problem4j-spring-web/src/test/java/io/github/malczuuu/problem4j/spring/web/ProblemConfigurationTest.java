package io.github.malczuuu.problem4j.spring.web;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {JacksonAutoConfiguration.class, ProblemConfiguration.class})
class ProblemConfigurationTest {

  @Test
  void contextLoads() {}
}
