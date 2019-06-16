package com.evbox.everon.errorhandling;

import lombok.Value;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Value
class ApiExceptionResponse {

    private LocalDateTime timestamp;

    private HttpStatus status;

    private List<String> errors;

    static ApiExceptionResponse of(HttpStatus httpStatus, String... errors) {

        return new ApiExceptionResponse(LocalDateTime.now(), httpStatus, Arrays.asList(errors));
    }

    static ApiExceptionResponse of(HttpStatus httpStatus, List<String> errors) {

        return new ApiExceptionResponse(LocalDateTime.now(), httpStatus, errors);
    }

}
