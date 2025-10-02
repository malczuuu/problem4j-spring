package io.github.malczuuu.problem4j.spring.webflux;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class DecodingExceptionFluxAdvice {

  @ExceptionHandler(DecodingException.class)
  public Mono<ResponseEntity<Problem>> handleDecodingException(
      DecodingException ex, ServerWebExchange exchange) {
    HttpStatus status = HttpStatus.BAD_REQUEST;

    Problem problem = Problem.builder().status(ProblemStatus.BAD_REQUEST).build();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

    return Mono.just(new ResponseEntity<>(problem, headers, status));
  }
}
