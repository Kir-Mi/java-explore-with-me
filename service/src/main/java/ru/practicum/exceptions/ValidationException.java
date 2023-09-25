package ru.practicum.exceptions;

import org.springframework.http.HttpStatus;

public class ValidationException extends MyApplicationExceptions {
    public ValidationException(final String errorMessage, HttpStatus httpStatus) {
        super(errorMessage, httpStatus);
    }
}
