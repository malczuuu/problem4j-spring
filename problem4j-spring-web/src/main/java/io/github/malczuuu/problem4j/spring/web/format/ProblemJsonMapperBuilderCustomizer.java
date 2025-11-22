package io.github.malczuuu.problem4j.spring.web.format;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.jackson3.ProblemJacksonMixIn;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import tools.jackson.databind.json.JsonMapper;

/**
 * Customizes Spring Boot's JSON ObjectMapper by registering a mix-in for the {@link Problem}
 * interface. Ensures that all Problem objects are serialized and deserialized consistently
 * according to {@link ProblemJacksonMixIn}.
 */
public class ProblemJsonMapperBuilderCustomizer implements JsonMapperBuilderCustomizer {

  /**
   * Adds the {@link ProblemJacksonMixIn} to the JSON mapper builder for proper serialization and
   * deserialization of {@link Problem} objects.
   *
   * @param builder the JSON mapper builder to customize
   */
  @Override
  public void customize(JsonMapper.Builder builder) {
    builder.addMixIn(Problem.class, ProblemJacksonMixIn.class);
  }
}
