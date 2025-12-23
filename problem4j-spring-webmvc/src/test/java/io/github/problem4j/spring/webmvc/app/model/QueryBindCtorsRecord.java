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
package io.github.problem4j.spring.webmvc.app.model;

import static io.github.problem4j.spring.webmvc.app.model.ModelUtils.safeParseInt;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.BindParam;

public record QueryBindCtorsRecord(
    @NotNull @Size(min = 1, max = 5) String text,
    @BindParam("num") @NotNull @Positive Integer number) {

  public QueryBindCtorsRecord(String text) {
    this(text, safeParseInt(text));
  }
}
