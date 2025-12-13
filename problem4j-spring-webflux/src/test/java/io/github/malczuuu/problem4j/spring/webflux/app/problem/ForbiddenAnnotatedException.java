package io.github.malczuuu.problem4j.spring.webflux.app.problem;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenAnnotatedException extends RuntimeException {}
