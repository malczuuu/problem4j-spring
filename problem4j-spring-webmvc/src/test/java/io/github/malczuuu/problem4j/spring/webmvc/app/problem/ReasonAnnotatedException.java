package io.github.malczuuu.problem4j.spring.webmvc.app.problem;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "this is reason")
public class ReasonAnnotatedException extends RuntimeException {}
