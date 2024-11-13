package com.reliaquest.api.validator;

import static org.junit.jupiter.api.Assertions.*;

import com.reliaquest.api.entity.CreateEmployeeDTO;
import com.reliaquest.api.exceptions.ValidationException;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

class InputValidatorTest {

    @Test
    void convertAndValidateEmployeeInput_Success() throws ValidationException {
        Map<String, Object> employeeInput = new HashMap<>();
        employeeInput.put("name", "John Doe");
        employeeInput.put("salary", "50000");
        employeeInput.put("age", "30");

        CreateEmployeeDTO result = InputValidator.convertAndValidateEmployeeInput(employeeInput);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("50000", result.getSalary());
        assertEquals("30", result.getAge());
    }

    @Test
    void convertAndValidateEmployeeInput_MissingName() {
        Map<String, Object> employeeInput = new HashMap<>();
        employeeInput.put("salary", "50000");
        employeeInput.put("age", "30");

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            InputValidator.convertAndValidateEmployeeInput(employeeInput);
        });

        assertEquals("Name is either empty or invalid", exception.getError().getMessage());
        assertEquals("ERR-302", exception.getError().getCode());
    }

    @Test
    void convertAndValidateEmployeeInput_InvalidSalaryType() {
        Map<String, Object> employeeInput = new HashMap<>();
        employeeInput.put("name", "John Doe");
        employeeInput.put("salary", 50000);
        employeeInput.put("age", "30");

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            InputValidator.convertAndValidateEmployeeInput(employeeInput);
        });

        assertEquals("Salary is either missing or invalid", exception.getError().getMessage());
        assertEquals("ERR-303", exception.getError().getCode());
    }

    @Test
    void convertAndValidateEmployeeInput_InvalidAgeType() {
        Map<String, Object> employeeInput = new HashMap<>();
        employeeInput.put("name", "John Doe");
        employeeInput.put("salary", "50000");
        employeeInput.put("age", 30);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            InputValidator.convertAndValidateEmployeeInput(employeeInput);
        });

        assertEquals("Age is either empty or invalid", exception.getError().getMessage());
        assertEquals("ERR-305", exception.getError().getCode());
    }

    @Test
    void convertAndValidateEmployeeInput_NegativeSalary() {
        Map<String, Object> employeeInput = new HashMap<>();
        employeeInput.put("name", "John Doe");
        employeeInput.put("salary", "-50000");
        employeeInput.put("age", "30");

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            InputValidator.convertAndValidateEmployeeInput(employeeInput);
        });

        assertEquals("Salary must be non-negative", exception.getError().getMessage());
        assertEquals("ERR-304", exception.getError().getCode());
    }

    @Test
    void convertAndValidateEmployeeInput_NegativeAge() {
        Map<String, Object> employeeInput = new HashMap<>();
        employeeInput.put("name", "John Doe");
        employeeInput.put("salary", "50000");
        employeeInput.put("age", "-30");

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            InputValidator.convertAndValidateEmployeeInput(employeeInput);
        });

        assertEquals("Age must be a positive value", exception.getError().getMessage());
        assertEquals("ERR-306", exception.getError().getCode());
    }

    @Test
    void convertAndValidateEmployeeInput_AgeMoreThan100() {
        Map<String, Object> employeeInput = new HashMap<>();
        employeeInput.put("name", "John Doe");
        employeeInput.put("salary", "50000");
        employeeInput.put("age", "130");

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            InputValidator.convertAndValidateEmployeeInput(employeeInput);
        });

        assertEquals("Age cannot be above 100", exception.getError().getMessage());
        assertEquals("ERR-307", exception.getError().getCode());
    }
}