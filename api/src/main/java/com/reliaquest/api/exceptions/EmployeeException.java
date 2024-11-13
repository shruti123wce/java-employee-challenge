package com.reliaquest.api.exceptions;

public class EmployeeException extends RuntimeException {
    private final ErrorCode error;

    public EmployeeException( ErrorCode error){
        super(error.getMessage());
        this.error = error;
    }

    public ErrorCode getError() {
        return error;
    }
}

