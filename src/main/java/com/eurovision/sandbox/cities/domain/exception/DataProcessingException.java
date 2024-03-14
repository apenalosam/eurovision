package com.eurovision.sandbox.cities.domain.exception;

import org.springframework.http.HttpStatus;

public class DataProcessingException extends ProcessingException {

    private static final String TYPE_MESSAGE_CODE = "DataProcessingError";

    private DataProcessingException(final String title, final String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, TYPE_MESSAGE_CODE, title, message);
    }

    private DataProcessingException(final String title, final String message, Object... parameters) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, TYPE_MESSAGE_CODE, title, message, parameters);
    }

    public static DataProcessingException of(final String message) {
        return new DataProcessingException(message, message);
    }

    public static DataProcessingException of(final String title, final String message) {
        return new DataProcessingException(title, message);
    }

    public static DataProcessingException of(final String message, Object... parameters) {
        return new DataProcessingException(message, message, parameters);
    }

    public static DataProcessingException of(final String title, final String message, Object... parameters) {
        return new DataProcessingException(title, message, parameters);
    }

}
