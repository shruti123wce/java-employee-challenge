package com.reliaquest.api.exceptions;

public enum ErrorCode {
    API_REQUEST_FAILURE("ERR-101", "REST API request execution failed"),
    JSON_DESERIALIZATION_FAILURE("ERR-102", "Failed to parse JSON data"),
    NO_RECORDS_FOUND("ERR-201", "Employee data not found"),
    EMPLOYEE_NAME_NOT_FOUND("ERR-202", "No matching employees for the provided name"),
    MISSING_ID("ERR-301", "Employee ID cannot be empty"),
    INVALID_OR_EMPTY_NAME("ERR-302", "Name is either empty or invalid"),
    INVALID_OR_MISSING_SALARY("ERR-303", "Salary is either missing or invalid"),
    SALARY_BELOW_ZERO("ERR-304", "Salary must be non-negative"),
    INVALID_OR_EMPTY_AGE("ERR-305", "Age is either empty or invalid"),
    AGE_BELOW_ZERO("ERR-306", "Age must be a positive value"),
    AGE_OVER_LIMIT("ERR-307", "Age cannot be above 100");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
