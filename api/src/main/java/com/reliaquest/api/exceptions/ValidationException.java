package com.reliaquest.api.exceptions;

public class ValidationException  extends RuntimeException {
    private final ErrorCode error;

    public ValidationException(ErrorCode error) {
        this.error = error;
    }

    public ErrorCode getError() {
        return error;
    }
}