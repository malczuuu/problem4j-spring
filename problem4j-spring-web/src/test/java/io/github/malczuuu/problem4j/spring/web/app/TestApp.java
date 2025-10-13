package io.github.malczuuu.problem4j.spring.web.app;

import jakarta.annotation.PostConstruct;
import java.util.Locale;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TestApp {

  @PostConstruct
  public void postConstruct() {
    Locale.setDefault(Locale.ENGLISH);
  }
}
