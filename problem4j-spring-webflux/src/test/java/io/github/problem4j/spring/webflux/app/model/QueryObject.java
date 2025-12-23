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
package io.github.problem4j.spring.webflux.app.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class QueryObject {

  @NotNull
  @Size(min = 1, max = 5)
  private String text;

  @NotNull @Positive private Integer number;

  public QueryObject() {}

  public String getText() {
    return text;
  }

  public Integer getNumber() {
    return number;
  }

  public void setText(String text) {
    this.text = text;
  }

  public void setNumber(Integer number) {
    this.number = number;
  }
}
