package edu.csye.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MandatoryFieldValueMissingException extends RuntimeException {
	public MandatoryFieldValueMissingException(String message) {
		super(message);
	}

}
