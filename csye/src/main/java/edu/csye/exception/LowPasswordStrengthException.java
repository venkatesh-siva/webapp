package edu.csye.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class LowPasswordStrengthException extends RuntimeException {
    public LowPasswordStrengthException(String message) {
        super(message);
    }
}
