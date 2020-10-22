package edu.csye.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ImageExistException extends RuntimeException {
	public ImageExistException(String message) {
        super(message);
    }
}
