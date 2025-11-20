package io.github.malczuuu.problem4j.spring.web.util;

import java.util.Objects;

/**
 * Implementation of {@link ClassDistanceEvaluation} that calculates inheritance distance using a
 * recursive graph-based approach. Supports limiting the maximum depth to prevent stack overflow in
 * deep or complex hierarchies.
 */
public class GraphClassDistanceEvaluation implements ClassDistanceEvaluation {

  public static final int DEFAULT_MAX_DEPTH = 50;

  private final int defaultMaxDepth;

  /** Creates a {@link GraphClassDistanceEvaluation} using the default maximum depth. */
  public GraphClassDistanceEvaluation() {
    this(DEFAULT_MAX_DEPTH);
  }

  /**
   * Creates a {@link GraphClassDistanceEvaluation} with a custom default maximum depth.
   *
   * @param defaultMaxDepth the maximum depth to use for inheritance calculations
   */
  public GraphClassDistanceEvaluation(int defaultMaxDepth) {
    this.defaultMaxDepth = defaultMaxDepth;
  }

  /**
   * Calculates the inheritance distance between two class types.
   *
   * <p>The distance represents how far target type is from the base type in the class hierarchy. If
   * the types are not compatible, {@link Integer#MAX_VALUE} is returned.
   *
   * @param target the class whose distance is being measured
   * @param base the class to measure distance to
   * @param maxDepth the maximum depth to traverse in the inheritance hierarchy
   * @return number of superclass steps between the two types, or {@link Integer#MAX_VALUE} if not
   *     assignable
   */
  @Override
  public int calculate(Class<?> target, Class<?> base, int maxDepth) {
    return calculateInternal(target, base, 0, maxDepth);
  }

  @Override
  public int getDefaultMaxDepth() {
    return defaultMaxDepth;
  }

  private int calculateInternal(Class<?> target, Class<?> base, int currentDepth, int maxDepth) {
    if (currentDepth > maxDepth || Objects.equals(target, base)) {
      return 0;
    }

    if (!base.isAssignableFrom(target)) {
      return Integer.MAX_VALUE;
    }

    int minDistance = Integer.MAX_VALUE;

    Class<?> superclass = target.getSuperclass();
    if (superclass != null) {
      int distance = calculateInternal(superclass, base, currentDepth + 1, maxDepth);
      if (distance != Integer.MAX_VALUE) {
        minDistance = distance + 1;
      }
    }

    for (Class<?> iface : target.getInterfaces()) {
      int distance = calculateInternal(iface, base, currentDepth + 1, maxDepth);
      if (distance != Integer.MAX_VALUE) {
        minDistance = Math.min(minDistance, distance + 1);
      }
    }

    return minDistance;
  }
}
