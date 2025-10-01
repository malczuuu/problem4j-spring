package io.github.malczuuu.problem4j.spring.web;

import io.github.malczuuu.problem4j.jackson.ProblemModule;
import io.github.malczuuu.problem4j.spring.web.formatting.DetailFormatting;
import io.github.malczuuu.problem4j.spring.web.formatting.FieldNameFormatting;
import io.github.malczuuu.problem4j.spring.web.formatting.FormattingConfiguration;
import io.github.malczuuu.problem4j.spring.web.mapping.ExceptionMapping;
import io.github.malczuuu.problem4j.spring.web.mapping.ExceptionMappingConfiguration;
import io.github.malczuuu.problem4j.spring.web.validation.ConstraintViolationExceptionAdvice;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Spring Boot autoconfiguration for problem-based exception handling.
 *
 * <p>This class wires all necessary beans for producing standardized {@link
 * io.github.malczuuu.problem4j.core.Problem} responses from Spring MVC controllers. It includes:
 *
 * <ul>
 *   <li>Jackson module registration via {@link io.github.malczuuu.problem4j.jackson.ProblemModule}.
 *   <li>Exception handling beans such as {@link ProblemEnhancedExceptionHandler}, {@link
 *       ProblemExceptionAdvice}, and {@link ExceptionAdvice}.
 *   <li>Validation-specific handling via {@link ConstraintViolationExceptionAdvice}.
 * </ul>
 *
 * <p>Beans are conditional:
 *
 * <ul>
 *   <li>{@link ConditionalOnMissingBean} ensures user-defined beans override defaults.
 *   <li>{@link ConditionalOnClass} ensures compatibility with optional framework classes.
 * </ul>
 *
 * <p>The configuration also imports supporting configurations for exception mappings ({@link
 * io.github.malczuuu.problem4j.spring.web.mapping.ExceptionMappingConfiguration}) and formatting
 * ({@link io.github.malczuuu.problem4j.spring.web.formatting.FormattingConfiguration}).
 */
@ConditionalOnClass(ResponseEntityExceptionHandler.class)
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(WebMvcAutoConfiguration.class)
@EnableConfigurationProperties(ProblemProperties.class)
@Import({FormattingConfiguration.class, ExceptionMappingConfiguration.class})
public class ProblemAutoConfiguration {

  @ConditionalOnMissingBean(ProblemModule.class)
  @Bean
  public ProblemModule problemModule() {
    return new ProblemModule();
  }

  @Order(Ordered.LOWEST_PRECEDENCE - 10)
  @ConditionalOnMissingBean(ResponseEntityExceptionHandler.class)
  @Bean
  public ResponseEntityExceptionHandler responseEntityExceptionHandler(
      List<ExceptionMapping> exceptionMappings) {
    return new ProblemEnhancedExceptionHandler(new ExceptionMappingRegistry(exceptionMappings));
  }

  @Order(Ordered.LOWEST_PRECEDENCE)
  @ConditionalOnMissingBean(ExceptionAdvice.class)
  @Bean
  public ExceptionAdvice exceptionAdvice() {
    return new ExceptionAdvice();
  }

  @Order(Ordered.LOWEST_PRECEDENCE - 10)
  @ConditionalOnMissingBean(ProblemExceptionAdvice.class)
  @Bean
  public ProblemExceptionAdvice problemExceptionAdvice() {
    return new ProblemExceptionAdvice();
  }

  @ConditionalOnClass(ConstraintViolationException.class)
  @Configuration(proxyBeanMethods = false)
  public static class ConstraintViolationExceptionAdviceConfiguration {

    @Order(Ordered.LOWEST_PRECEDENCE - 10)
    @ConditionalOnMissingBean(ConstraintViolationExceptionAdvice.class)
    @Bean
    public ConstraintViolationExceptionAdvice constraintViolationExceptionAdvice(
        DetailFormatting detailFormatting, FieldNameFormatting fieldNameFormatting) {
      return new ConstraintViolationExceptionAdvice(detailFormatting, fieldNameFormatting);
    }
  }
}
