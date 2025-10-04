package io.github.malczuuu.problem4j.spring.webflux.error;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;

@Configuration
public class ProblemErrorWebFluxAutoConfiguration {

  private final ServerProperties serverProperties;

  public ProblemErrorWebFluxAutoConfiguration(ServerProperties serverProperties) {
    this.serverProperties = serverProperties;
  }

  /**
   * Overrides {@link ErrorWebExceptionHandler} defined in {@code ErrorWebFluxAutoConfiguration} to
   * return problem JSONs. Must be declared with {@code @Order(-2)}, as default one declared in
   * {@code ErrorWebFluxAutoConfiguration} has {@code @Order(-1)} (the lower value wins).
   *
   * @see org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration
   */
  @Bean
  @ConditionalOnMissingBean(value = ErrorWebExceptionHandler.class, search = SearchStrategy.CURRENT)
  @Order(-2)
  public ErrorWebExceptionHandler errorWebExceptionHandler(
      ErrorAttributes errorAttributes,
      WebProperties webProperties,
      ObjectProvider<ViewResolver> viewResolvers,
      ServerCodecConfigurer serverCodecConfigurer,
      ApplicationContext applicationContext) {
    ProblemErrorWebExceptionHandler exceptionHandler =
        new ProblemErrorWebExceptionHandler(
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
