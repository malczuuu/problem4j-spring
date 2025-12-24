package io.github.problem4j.spring.web.autoconfigure;

import io.github.problem4j.spring.web.parameter.BindingResultSupport;
import io.github.problem4j.spring.web.parameter.DefaultBindingResultSupport;
import io.github.problem4j.spring.web.parameter.DefaultMethodParameterSupport;
import io.github.problem4j.spring.web.parameter.DefaultMethodValidationResultSupport;
import io.github.problem4j.spring.web.parameter.MethodParameterSupport;
import io.github.problem4j.spring.web.parameter.MethodValidationResultSupport;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.method.MethodValidationResult;

/**
 * Configuration for parameter support components, such as method parameter name resolution and
 * binding / method validation result conversion.
 */
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.ANY)
@Configuration(proxyBeanMethods = false)
class ProblemParameterConfiguration {

  /**
   * Provides a default {@link MethodParameterSupport} bean if none is defined by the user.
   *
   * @return a new {@link DefaultMethodParameterSupport}
   */
  @ConditionalOnMissingBean(MethodParameterSupport.class)
  @Bean
  MethodParameterSupport problemMethodParameterSupport() {
    return new DefaultMethodParameterSupport();
  }

  @ConditionalOnClass(MethodValidationResult.class)
  @Configuration(proxyBeanMethods = false)
  static class ProblemMethodValidationConfiguration {

    /**
     * Provides a default {@link MethodValidationResultSupport} bean if none is defined by the user.
     *
     * @return a new {@link DefaultMethodValidationResultSupport}
     */
    @ConditionalOnMissingBean(MethodValidationResultSupport.class)
    @Bean
    MethodValidationResultSupport problemMethodValidationResultSupport(
        MethodParameterSupport methodParameterSupport) {
      return new DefaultMethodValidationResultSupport(methodParameterSupport);
    }
  }

  /**
   * Provides a default {@link BindingResultSupport} bean that uses field names for violations.
   *
   * @return a new {@link DefaultBindingResultSupport}
   */
  @ConditionalOnMissingBean(BindingResultSupport.class)
  @Bean
  BindingResultSupport problemBindingSupport() {
    return new DefaultBindingResultSupport();
  }
}
