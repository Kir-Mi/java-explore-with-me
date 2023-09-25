package ru.practicum.exceptions;

import org.springframework.http.HttpStatus;

public class MyApplicationExceptions extends RuntimeException {

    private HttpStatus httpStatus;
    private String errorMessage;

    public MyApplicationExceptions(String errorMessage, HttpStatus httpStatus) {
        this.errorMessage = errorMessage;
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
