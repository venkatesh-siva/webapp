package edu.csye.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnsupportedImageException extends RuntimeException{
	public UnsupportedImageException(String message){
	    super(message);
	  }
}
