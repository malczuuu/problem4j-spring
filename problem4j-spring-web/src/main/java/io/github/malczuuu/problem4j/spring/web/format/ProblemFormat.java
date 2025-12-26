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
package io.github.malczuuu.problem4j.spring.web.format;

/**
 * Defines a contract for formatting problem detail field and property names (mostly before they are
 * included in a {@code Problem} response).
 *
 * <p>Implementations can customize how details and property names are presented - for example, by
 * applying localization, case formatting, or message templating.
 *
 * @deprecated migrated to {@code io.github.problem4j:problem4j-spring-web} namespace.
 */
@Deprecated(since = "1.1.7")
public interface ProblemFormat {

  /** Format {@code detail} field of {@code Problem} model. */
  String formatDetail(String detail);
}
