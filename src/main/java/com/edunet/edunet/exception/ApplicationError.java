package com.edunet.edunet.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ApplicationError extends RuntimeException {

    public ApplicationError(String message) {
        super(message);
    }
}
