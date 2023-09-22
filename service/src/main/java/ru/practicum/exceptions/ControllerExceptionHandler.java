package ru.practicum.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;
import java.util.Map;

@ControllerAdvice("ru.practicum")
@Slf4j
public class ControllerExceptionHandler {

    @ExceptionHandler(MyApplicationExceptions.class)
    public ResponseEntity<Object> handleConflict(MyApplicationExceptions ex) {
        HttpStatus responseStatus = ex.getHttpStatus();
        if (responseStatus.is4xxClientError()) {
            log.warn(ex.toString());
        } else if (responseStatus.is5xxServerError()) {
            log.warn(ex.toString());
        } else {
            log.debug(ex.toString());
        }

        return ResponseEntity
                .status(responseStatus)
                .body(Map.of("error", ex.getErrorMessage()));
    }

    @ExceptionHandler({DataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleConflictHttp(final DataIntegrityViolationException e) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(final ConstraintViolationException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
    }
}
