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
package io.github.problem4j.spring.webflux.app.rest;

import io.github.problem4j.spring.webflux.app.problem.AnnotatedException;
import io.github.problem4j.spring.webflux.app.problem.AnnotationEmptyException;
import io.github.problem4j.spring.webflux.app.problem.ExtendedException;
import io.github.problem4j.spring.webflux.app.problem.ResolvableException;
import io.github.problem4j.spring.webflux.app.problem.UnresolvableException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/problem-advice")
public class ProblemAdviceController {

  @GetMapping(path = "/exception")
  public String exception(
      @RequestParam("value1") String value1,
      @RequestParam("value2") Long value2,
      @RequestParam("value3") boolean value3) {
    throw new ExtendedException(value1, value2, value3);
  }

  @GetMapping(path = "/annotation")
  public String annotation(
      @RequestParam("value1") String value1,
      @RequestParam("value2") Long value2,
      @RequestParam("value3") boolean value3) {
    throw new AnnotatedException(value1, value2, value3);
  }

  @GetMapping(path = "/annotation-empty")
  public String annotationEmpty() {
    throw new AnnotationEmptyException("does not matter", -1L, false);
  }

  @GetMapping(path = "/resolvable")
  public String resolvable() {
    throw new ResolvableException();
  }

  @GetMapping(path = "/unresolvable")
  public String unresolvable() {
    throw new UnresolvableException();
  }
}
