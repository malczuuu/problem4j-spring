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
package io.github.problem4j.spring.webflux;

import static io.github.problem4j.spring.web.context.AttributeSupport.TRACE_ID;

import org.slf4j.Logger;
import org.springframework.web.server.ServerWebExchange;

class WebFluxAdviceSupport {

  static void logAdviceException(
      Logger log, Exception ex, ServerWebExchange exchange, Exception e) {
    log.warn(
        "Unable to resolve problem response (method={}, path={}, traceId={}, message={}, originalException=[{} : {}])",
        exchange.getRequest().getMethod(),
        exchange.getRequest().getPath(),
        exchange.getAttribute(TRACE_ID),
        e.getMessage(),
        ex.getClass().getName(),
        ex.getMessage(),
        e);
  }
}
