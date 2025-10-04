package io.github.malczuuu.problem4j.spring.webflux.integration;

import jakarta.annotation.PostConstruct;
import java.util.Locale;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

/**
 * Name starts with {@code _} so it's always sorted on top (or bottom, depending on OS) of
 * directory.
 */
@SpringBootApplication
class _TestApp {

  @Configuration(proxyBeanMethods = false)
  public static class TestLocaleConfig {

    @PostConstruct
    public void init() {
      Locale.setDefault(Locale.ENGLISH);
    }
  }
}
