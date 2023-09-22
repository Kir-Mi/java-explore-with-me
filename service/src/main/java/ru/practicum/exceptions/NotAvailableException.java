package ru.practicum.exceptions;

import org.springframework.http.HttpStatus;

public class NotAvailableException extends MyApplicationExceptions {
    public NotAvailableException(final String errorMessage, HttpStatus httpStatus) {
        super(errorMessage, httpStatus);
    }
}
