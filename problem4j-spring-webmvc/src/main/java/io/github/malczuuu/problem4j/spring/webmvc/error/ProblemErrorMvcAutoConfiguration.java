package io.github.malczuuu.problem4j.spring.webmvc.error;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProblemErrorMvcAutoConfiguration {

  @ConditionalOnMissingBean(value = ErrorAttributes.class)
  @Bean
  public ErrorAttributes errorAttributes() {
    return new DefaultErrorAttributes();
  }

  @ConditionalOnMissingBean(value = ErrorController.class)
  @Bean
  public ErrorController errorController(ErrorAttributes errorAttributes) {
    return new ProblemErrorController(errorAttributes);
  }
}
