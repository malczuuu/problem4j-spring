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
package io.github.malczuuu.problem4j.spring.web;

import io.github.malczuuu.problem4j.spring.web.resolver.ProblemResolver;
import io.github.malczuuu.problem4j.spring.web.util.ClassDistanceEvaluation;
import java.util.List;

/**
 * {@link ProblemResolverStore} implementation evaluates resolved based on class and its
 * inheritance.
 *
 * <p>Resolvers are matched to exceptions by assignability, preferring the most specific exception
 * type.
 */
public class DefaultProblemResolverStore extends AbstractProblemResolverStore {

  /**
   * Creates a new store initialized with the given resolvers.
   *
   * @param problemResolvers list of available {@link ProblemResolver} instances
   * @throws NullPointerException if any resolver or its exception class is {@code null}
   */
  public DefaultProblemResolverStore(List<ProblemResolver> problemResolvers) {
    super(problemResolvers);
  }

  /**
   * Creates a new store initialized with the given resolvers and a specific class distance
   * evaluation strategy.
   *
   * @param problemResolvers list of available {@link ProblemResolver} instances
   * @param classDistanceEvaluation the strategy used to evaluate the distance between exception
   *     classes (e.g., when finding the best resolver for a specific exception type).
   * @throws NullPointerException if any resolver or its exception class is {@code null}
   */
  public DefaultProblemResolverStore(
      List<ProblemResolver> problemResolvers, ClassDistanceEvaluation classDistanceEvaluation) {
    super(problemResolvers, classDistanceEvaluation);
  }
}
