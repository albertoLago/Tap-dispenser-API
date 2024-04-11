package com.rviewer.skeletons.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class DispenserAlreadyClosedOpenedException extends RuntimeException {
    public DispenserAlreadyClosedOpenedException(String message) {
        super(message);
    }
}
