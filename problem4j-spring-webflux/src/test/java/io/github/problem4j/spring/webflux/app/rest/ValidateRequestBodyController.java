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

import io.github.problem4j.spring.webflux.app.model.AlwaysInvalidRequest;
import io.github.problem4j.spring.webflux.app.model.TestRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ValidateRequestBodyController {

  @PostMapping("/validate-request-body")
  public String validateRequestBody(@Valid @RequestBody TestRequest request) {
    return "OK";
  }

  @PostMapping("/validate-global-object")
  public String validateGlobalObject(@Valid @RequestBody AlwaysInvalidRequest body) {
    return "OK";
  }
}
