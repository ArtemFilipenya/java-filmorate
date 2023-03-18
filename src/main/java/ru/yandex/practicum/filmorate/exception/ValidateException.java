package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;

public class ValidateException extends RuntimeException {
    private HttpStatus code;
    public ValidateException(final String message) {
        super(message);
    }

    public ValidateException(HttpStatus code, final String message) {
        super(message);
        this.code = code;
    }
}