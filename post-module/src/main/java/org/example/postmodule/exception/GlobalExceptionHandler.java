package org.example.postmodule.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<DefaultErrorMessage> handleEntityNotFound(EntityNotFoundException exception, HttpServletRequest httpServletRequest) {
        DefaultErrorMessage errorMessage = DefaultErrorMessage.builder()
                .title("Incorrect user request")
                .detail(exception.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .errorCode("ENTITY_NOT_FOUND")
                .instance(httpServletRequest.getRequestURI())
                .fieldErrors(null)
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(errorMessage);
    }

    //Сделать обработку остальных ошибок
}
