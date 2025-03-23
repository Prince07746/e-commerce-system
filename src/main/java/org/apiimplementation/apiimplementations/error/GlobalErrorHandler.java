package org.apiimplementation.apiimplementations.error;

import org.apiimplementation.apiimplementations.config.Message;
import org.apiimplementation.apiimplementations.error.customError.InsufficientProductException;
import org.apiimplementation.apiimplementations.error.customError.InvalidFormatException;
import org.apiimplementation.apiimplementations.error.customError.NotFoundException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.naming.InsufficientResourcesException;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalErrorHandler {


    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<Message> badRequest(InvalidFormatException ex){
        return ResponseEntity.badRequest().body(new Message(ex.getMessage()));
    }

    @ExceptionHandler(InsufficientProductException.class)
    public ResponseEntity<Message> inSufficientProduct(InsufficientProductException ex){
        return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(new Message(ex.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Message> noSuchElement(NotFoundException ex){
        return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(new Message(ex.getMessage()));
    }




}
