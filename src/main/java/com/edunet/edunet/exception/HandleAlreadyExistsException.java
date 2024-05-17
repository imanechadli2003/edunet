package com.edunet.edunet.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class HandleAlreadyExistsException extends RuntimeException {

    public HandleAlreadyExistsException(String handle) {
        super(String.format("handle %s already exists", handle));
    }
}
