package io.github.malczuuu.problem4j.spring.webflux.app;

import jakarta.annotation.PostConstruct;
import java.util.Locale;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
public class WebFluxTestApp {

  @Configuration(proxyBeanMethods = false)
  public static class TestLocaleConfig {

    @PostConstruct
    public void init() {
      Locale.setDefault(Locale.ENGLISH);
    }
  }
}
