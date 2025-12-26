/*
 * Copyright (c) 2025 Damian Malczewski
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * SPDX-License-Identifier: MIT
 */
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
 *
 * @deprecated migrated to {@code io.github.problem4j:problem4j-spring-web} namespace.
 */
@Deprecated(since = "1.1.7")
public enum HierarchyTraversalMode {

  /** Follow the superclass chain when calculating distance. */
  SUPERCLASS,

  /** Follow implemented interfaces when calculating distance. */
  INTERFACES
}
