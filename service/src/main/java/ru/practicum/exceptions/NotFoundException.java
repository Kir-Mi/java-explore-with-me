package ru.practicum.exceptions;

import org.springframework.http.HttpStatus;

public class NotFoundException extends MyApplicationExceptions {

    public NotFoundException(String errorMessage, HttpStatus httpStatus) {
        super(errorMessage, httpStatus);
    }
}
