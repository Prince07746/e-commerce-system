package org.apiimplementation.apiimplementations.error.customError;

public class InsufficientProductException extends RuntimeException{
    public InsufficientProductException(String message){
        super(message);
    }
}
