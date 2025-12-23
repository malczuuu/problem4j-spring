package io.github.problem4j.spring.web.parameter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.validation.method.MethodValidationResult;
import org.springframework.web.bind.annotation.BindParam;

/**
 * Configuration for parameter support components, such as method parameter name resolution and
 * binding / method validation result conversion.
 */
@Configuration(proxyBeanMethods = false)
public class ProblemParameterConfiguration {

  /**
   * Provides a default {@link MethodParameterSupport} bean if none is defined by the user.
   *
   * @return a new {@link DefaultMethodParameterSupport}
   */
  @ConditionalOnMissingBean(MethodParameterSupport.class)
  @Bean
  public MethodParameterSupport problemMethodParameterSupport() {
    return new DefaultMethodParameterSupport();
  }

  @ConditionalOnClass(MethodValidationResult.class)
  @Configuration(proxyBeanMethods = false)
  public static class ProblemMethodValidationConfiguration {

    /**
     * Provides a default {@link MethodValidationResultSupport} bean if none is defined by the user.
     *
     * @return a new {@link DefaultMethodValidationResultSupport}
     */
    @ConditionalOnMissingBean(MethodValidationResultSupport.class)
    @Bean
    public MethodValidationResultSupport problemMethodValidationResultSupport(
        MethodParameterSupport methodParameterSupport) {
      return new DefaultMethodValidationResultSupport(methodParameterSupport);
    }
  }

  @Order(0)
  @ConditionalOnClass(BindParam.class)
  @Configuration(proxyBeanMethods = false)
  public static class ProblemBeanParamAwareBindingConfiguration {

    /**
     * Provides a {@link BindingResultSupport} bean that handles {@code BindParam}-annotations.
     *
     * @return a new {@link BindParamAwareResultSupport}
     */
    @ConditionalOnMissingBean(BindingResultSupport.class)
    @Bean
    public BindingResultSupport problemBindingResultSupport() {
      return new BindParamAwareResultSupport();
    }
  }

  @Order(1)
  @Configuration(proxyBeanMethods = false)
  public static class ProblemDefaultBindingConfiguration {

    /**
     * Provides a default {@link BindingResultSupport} bean that uses field names for violations.
     *
     * @return a new {@link DefaultBindingResultSupport}
     */
    @ConditionalOnMissingBean(BindingResultSupport.class)
    @Bean
    public BindingResultSupport problemBindingSupport() {
      return new DefaultBindingResultSupport();
    }
  }
}
