package com.eurovision.sandbox.cities.domain.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class ProcessingException extends RuntimeException{

    private final String message;

    private final String typeMessageCode;

    private final String titleMessageCode;

    private final List<Object> parameters = new ArrayList<>();

    private final HttpStatus httpStatus;

    public ProcessingException(final HttpStatus httpStatus, final String typeMessageCode, final String titleMessageCode,
                               final String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.typeMessageCode = typeMessageCode;
        this.titleMessageCode = titleMessageCode;
        this.message = message;
    }

    public ProcessingException(final HttpStatus httpStatus, final String typeMessageCode, final String titleMessageCode,
                               final String message, final Object... parameters) {
        super(message);
        this.httpStatus = httpStatus;
        this.typeMessageCode = typeMessageCode;
        this.titleMessageCode = titleMessageCode;
        this.message = message;
        if (parameters != null) {
            this.parameters.addAll(Arrays.asList(parameters));
        }
    }

}
