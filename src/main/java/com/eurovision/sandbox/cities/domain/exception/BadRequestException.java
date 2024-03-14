package com.eurovision.sandbox.cities.domain.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends ProcessingException {

    private static final String TYPE_MESSAGE_CODE = "BadRequest";

    private BadRequestException(final String title, final String message) {
        super(HttpStatus.BAD_REQUEST, TYPE_MESSAGE_CODE, title, message);
    }

    private BadRequestException(final String title, final String message, Object... parameters) {
        super(HttpStatus.BAD_REQUEST, TYPE_MESSAGE_CODE, title, message, parameters);
    }

    public static BadRequestException of(final String message) {
        return new BadRequestException(message, message);
    }

    public static BadRequestException of(final String title, final String message) {
        return new BadRequestException(title, message);
    }

    public static BadRequestException of(final String message, Object... parameters) {
        return new BadRequestException(message, message, parameters);
    }

    public static BadRequestException of(final String title, final String message, Object... parameters) {
        return new BadRequestException(title, message, parameters);
    }

}
