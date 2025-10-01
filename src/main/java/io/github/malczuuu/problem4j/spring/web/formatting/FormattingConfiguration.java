package io.github.malczuuu.problem4j.spring.web.formatting;

import io.github.malczuuu.problem4j.spring.web.ProblemProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.JacksonProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class FormattingConfiguration {

  @ConditionalOnMissingBean(DetailFormatting.class)
  @Bean
  public DetailFormatting detailFormatting(ProblemProperties properties) {
    return new DetailFormattingImpl(properties.getDetailFormat());
  }

  @ConditionalOnMissingBean(FieldNameFormatting.class)
  @Bean
  public FieldNameFormatting fieldNameFormatting(JacksonProperties properties) {
    return new FieldNameFormattingImpl(properties.getPropertyNamingStrategy());
  }
}
