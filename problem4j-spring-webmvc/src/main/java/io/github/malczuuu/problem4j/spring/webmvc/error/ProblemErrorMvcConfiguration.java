package io.github.malczuuu.problem4j.spring.webmvc.error;

import io.github.malczuuu.problem4j.spring.web.processor.ProblemPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures MVC error handling to return {@code application/problem+json} responses according to
 * RFC 7807.
 *
 * <p>This setup replaces Spring Boot’s default error controller {@link
 * org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration} with {@link
 * ProblemErrorController}, which renders {@link io.github.malczuuu.problem4j.core.Problem} objects
 * instead of HTML or plain JSON errors.
 */
@Configuration(proxyBeanMethods = false)
public class ProblemErrorMvcConfiguration {

  /**
   * Registers a default {@link ErrorAttributes} bean if none exists.
   *
   * <p>Used to expose error details to the {@link ProblemErrorController}.
   *
   * @return a default {@link DefaultErrorAttributes} instance
   */
  @ConditionalOnMissingBean(value = ErrorAttributes.class)
  @Bean
  public ErrorAttributes errorAttributes() {
    return new DefaultErrorAttributes();
  }

  /**
   * Registers a custom {@link ErrorController} that renders Problem JSON responses.
   *
   * <p>Replaces the default error controller when no other implementation is present.
   *
   * @param errorAttributes provides error information for requests
   * @return a new {@link ProblemErrorController} instance
   * @see org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration
   */
  @ConditionalOnMissingBean(value = ErrorController.class)
  @Bean
  public ErrorController errorController(
      ProblemPostProcessor problemPostProcessor, ErrorAttributes errorAttributes) {
    return new ProblemErrorController(problemPostProcessor, errorAttributes);
  }
}
