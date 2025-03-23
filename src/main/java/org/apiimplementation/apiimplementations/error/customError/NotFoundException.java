package org.apiimplementation.apiimplementations.error.customError;

public class NotFoundException extends RuntimeException{

    public NotFoundException(String message){
        super(message);
    }
}
