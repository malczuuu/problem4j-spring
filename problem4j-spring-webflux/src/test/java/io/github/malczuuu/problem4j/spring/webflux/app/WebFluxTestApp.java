package io.github.malczuuu.problem4j.spring.webflux.app;

import jakarta.annotation.PostConstruct;
import java.util.Locale;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebFluxTestApp {

  @PostConstruct
  public void postConstruct() {
    Locale.setDefault(Locale.ENGLISH);
  }
}
