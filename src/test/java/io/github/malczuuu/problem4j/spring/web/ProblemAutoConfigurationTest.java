package io.github.malczuuu.problem4j.spring.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {JacksonAutoConfiguration.class, ProblemAutoConfiguration.class})
class ProblemAutoConfigurationTest {

  @Autowired private ProblemProperties properties;

  @Test
  void contextLoads() {
    assertThat(properties.getDetailFormat()).isEqualTo(ProblemProperties.DetailFormat.CAPITALIZED);
  }

  @Nested
  @SpringBootTest(
      classes = {JacksonAutoConfiguration.class, ProblemAutoConfiguration.class},
      properties = {"problem4j.detail-format=lowercase"})
  class PropertyOverrideLowercaseTest {

    @Autowired private ProblemProperties properties;

    @Test
    void contextLoads() {
      assertThat(properties.getDetailFormat()).isEqualTo(ProblemProperties.DetailFormat.LOWERCASE);
    }
  }

  @Nested
  @SpringBootTest(
      classes = {JacksonAutoConfiguration.class, ProblemAutoConfiguration.class},
      properties = {"problem4j.detail-format=uppercase"})
  class PropertyOverrideUppercaseTest {

    @Autowired private ProblemProperties properties;

    @Test
    void contextLoads() {
      assertThat(properties.getDetailFormat()).isEqualTo(ProblemProperties.DetailFormat.UPPERCASE);
    }
  }
}
