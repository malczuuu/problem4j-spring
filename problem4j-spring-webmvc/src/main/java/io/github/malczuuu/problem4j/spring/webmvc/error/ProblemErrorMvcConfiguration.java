package io.github.malczuuu.problem4j.spring.webmvc.error;

import io.github.malczuuu.problem4j.spring.web.processor.ProblemPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.webmvc.error.DefaultErrorAttributes;
import org.springframework.boot.webmvc.error.ErrorAttributes;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures MVC error handling to return {@code application/problem+json} responses according to
 * RFC 7807.
 *
 * <p>This setup replaces Spring Boot’s default error controller {@code ErrorMvcAutoConfiguration}
 * with {@link ProblemErrorController}, which renders {@code Problem} objects instead of HTML or
 * plain JSON errors.
 *
 * @see org.springframework.boot.webmvc.autoconfigure.error.ErrorMvcAutoConfiguration
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
  @ConditionalOnMissingBean(ErrorAttributes.class)
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
   * @see org.springframework.boot.webmvc.autoconfigure.error.ErrorMvcAutoConfiguration
   */
  @ConditionalOnMissingBean(ErrorController.class)
  @Bean
  public ErrorController errorController(
      ProblemPostProcessor problemPostProcessor, ErrorAttributes errorAttributes) {
    return new ProblemErrorController(problemPostProcessor, errorAttributes);
  }
}
