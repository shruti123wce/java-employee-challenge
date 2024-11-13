package com.reliaquest.api.exceptions;

public class InternalException extends RuntimeException{
    private final ErrorCode error;

    public InternalException(ErrorCode error){
        super(error.getMessage());
        this.error = error;
    }

    public ErrorCode getError() {
        return error;
    }
}
