package com.rviewer.skeletons.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class DispenserNotFoundException extends RuntimeException {
    public DispenserNotFoundException() {
        super("Requested dispenser does not exist");
    }
}
