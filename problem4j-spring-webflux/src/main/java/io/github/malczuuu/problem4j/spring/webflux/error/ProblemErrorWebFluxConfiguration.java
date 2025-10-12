package io.github.malczuuu.problem4j.spring.webflux.error;

import io.github.malczuuu.problem4j.spring.web.processor.ProblemPostProcessor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;

/**
 * Configures a custom {@link ErrorWebExceptionHandler} that produces {@code
 * application/problem+json} responses according to RFC 7807.
 *
 * <p>This configuration replaces Spring Boot’s default WebFlux error handler defined in {@link
 * org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration}.
 */
@Configuration(proxyBeanMethods = false)
public class ProblemErrorWebFluxConfiguration {

  private final ServerProperties serverProperties;

  public ProblemErrorWebFluxConfiguration(ServerProperties serverProperties) {
    this.serverProperties = serverProperties;
  }

  /**
   * Registers a default {@link ErrorAttributes} bean if none is already defined.
   *
   * <p>Provides the error details used by the {@link ProblemErrorWebExceptionHandler} to build
   * {@code application/problem+json} responses.
   *
   * @return a default {@link DefaultErrorAttributes} instance
   */
  @ConditionalOnMissingBean(ErrorAttributes.class)
  @Bean
  public ErrorAttributes errorAttributes() {
    return new DefaultErrorAttributes();
  }

  /**
   * Overrides {@link ErrorWebExceptionHandler} defined in {@code ErrorWebFluxAutoConfiguration} to
   * return problem JSONs. Must be declared with {@code @Order(-2)}, as default one declared in
   * {@code ErrorWebFluxAutoConfiguration} has {@code @Order(-1)} (the lower value wins).
   *
   * @see org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration
   */
  @Order(-2)
  @ConditionalOnMissingBean(ErrorWebExceptionHandler.class)
  @Bean
  public ErrorWebExceptionHandler errorWebExceptionHandler(
      ProblemPostProcessor problemPostProcessor,
      ErrorAttributes errorAttributes,
      WebProperties webProperties,
      ObjectProvider<ViewResolver> viewResolvers,
      ServerCodecConfigurer serverCodecConfigurer,
      ApplicationContext applicationContext) {
    ProblemErrorWebExceptionHandler exceptionHandler =
        new ProblemErrorWebExceptionHandler(
            problemPostProcessor,
            errorAttributes,
            webProperties.getResources(),
            this.serverProperties.getError(),
            applicationContext);
    exceptionHandler.setViewResolvers(viewResolvers.orderedStream().toList());
    exceptionHandler.setMessageWriters(serverCodecConfigurer.getWriters());
    exceptionHandler.setMessageReaders(serverCodecConfigurer.getReaders());
    return exceptionHandler;
  }
}
