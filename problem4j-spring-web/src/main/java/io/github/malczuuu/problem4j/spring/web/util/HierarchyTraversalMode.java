package io.github.malczuuu.problem4j.spring.web.util;

/**
 * Defines which parts of the class hierarchy are traversed by {@link GraphClassDistanceEvaluation}
 * during inheritance distance calculation.
 *
 * <ul>
 *   <li>{@link #SUPERCLASS} - traverse superclasses via {@link Class#getSuperclass()}
 *   <li>{@link #INTERFACES} - traverse implemented interfaces via {@link Class#getInterfaces()}
 * </ul>
 *
 * <p>Multiple modes can be combined to control the paths explored. If no modes are specified, both
 * are considered enabled by default.
 */
public enum HierarchyTraversalMode {

  /** Follow the superclass chain when calculating distance. */
  SUPERCLASS,

  /** Follow implemented interfaces when calculating distance. */
  INTERFACES
}
