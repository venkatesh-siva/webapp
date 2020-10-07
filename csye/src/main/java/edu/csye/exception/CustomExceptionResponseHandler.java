package edu.csye.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RestController
public class CustomExceptionResponseHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseBody
    public final ResponseEntity<Object> handleUserAlreadyExistsException(UserAlreadyExistsException ex, WebRequest request) {
        Response exceptionResponse = new Response(HttpStatus.BAD_REQUEST.toString(), ex.getMessage());
        return new ResponseEntity(exceptionResponse.toString(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidInputException.class)
    @ResponseBody
    public final ResponseEntity<Object> handleInvalidInputException(InvalidInputException ex, WebRequest request) {
        Response exceptionResponse = new Response(HttpStatus.BAD_REQUEST.toString(), ex.getMessage());
        return new ResponseEntity(exceptionResponse.toString(), HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(LowPasswordStrengthException.class)
    @ResponseBody
    public final ResponseEntity<Object> handleLowPasswordStrengthException(LowPasswordStrengthException ex, WebRequest request) {
        Response exceptionResponse = new Response(HttpStatus.BAD_REQUEST.toString(), ex.getMessage());
        return new ResponseEntity(exceptionResponse.toString(), HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(UserNotAuthorizedException.class)
    @ResponseBody
    public final ResponseEntity<Object> handleUserNotAuthorizedException(UserNotAuthorizedException ex, WebRequest request) {
        Response exceptionResponse = new Response(HttpStatus.BAD_REQUEST.toString(), ex.getMessage());
        return new ResponseEntity(exceptionResponse.toString(), HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseBody
    public final ResponseEntity<Object> hadleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        Response exceptionResponse = new Response(HttpStatus.BAD_REQUEST.toString(), ex.getMessage());
        return new ResponseEntity(exceptionResponse.toString(), HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(NoUpdateNeededException.class)
    @ResponseBody
    public final ResponseEntity<Object> handleNoUpdateNeededException(NoUpdateNeededException ex, WebRequest request) {
        Response exceptionResponse = new Response(HttpStatus.BAD_REQUEST.toString(), ex.getMessage());
        return new ResponseEntity(exceptionResponse.toString(), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(MandatoryFieldValueMissingException.class)
    @ResponseBody
    public final ResponseEntity<Object> handleNoUpdateNeededException(MandatoryFieldValueMissingException ex, WebRequest request) {
        Response exceptionResponse = new Response(HttpStatus.BAD_REQUEST.toString(), ex.getMessage());
        return new ResponseEntity(exceptionResponse.toString(), HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(QuestionNotFoundException.class)
    @ResponseBody
    public final ResponseEntity<Object> handleQuestionNotFoundException(QuestionNotFoundException ex, WebRequest request) {
        Response exceptionResponse = new Response(HttpStatus.NOT_FOUND.toString(), ex.getMessage());
        return new ResponseEntity(exceptionResponse.toString(), HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(AnswerNotFoundException.class)
    @ResponseBody
    public final ResponseEntity<Object> handleAnswerNotFoundException(AnswerNotFoundException ex, WebRequest request) {
        Response exceptionResponse = new Response(HttpStatus.NOT_FOUND.toString(), ex.getMessage());
        return new ResponseEntity(exceptionResponse.toString(), HttpStatus.NOT_FOUND);
    }

}