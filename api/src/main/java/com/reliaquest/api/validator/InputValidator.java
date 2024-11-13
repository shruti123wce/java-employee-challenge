package com.reliaquest.api.validator;


import com.reliaquest.api.exceptions.ValidationException;
import com.reliaquest.api.entity.CreateEmployeeDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static com.reliaquest.api.exceptions.ErrorCode.*;


@Slf4j
public class InputValidator {

    public static CreateEmployeeDTO convertAndValidateEmployeeInput(Map<String, Object> employeeInput) throws ValidationException {
        log.info("Starting validation for create employee input:{}", employeeInput);
        CreateEmployeeDTO createEmployeeDTO = new CreateEmployeeDTO();

        if (!employeeInput.containsKey("name") || !(employeeInput.get("name") instanceof String)) {
            throw new ValidationException(INVALID_OR_EMPTY_NAME);
        }
        createEmployeeDTO.setName((String) employeeInput.get("name"));

        if (!employeeInput.containsKey("salary") || !(employeeInput.get("salary") instanceof String salaryStr)) {
            throw new ValidationException(INVALID_OR_MISSING_SALARY);
        }
        if (Integer.parseInt(salaryStr) < 0) {
            throw new ValidationException(SALARY_BELOW_ZERO);
        }
        createEmployeeDTO.setSalary(salaryStr);

        if (!employeeInput.containsKey("age") || !(employeeInput.get("age") instanceof String ageStr)) {
            throw new ValidationException(INVALID_OR_EMPTY_AGE);
        }
        if (Integer.parseInt(ageStr) < 0) {
            throw new ValidationException(AGE_BELOW_ZERO);
        } else if (Integer.parseInt(ageStr) > 100) {
            throw new ValidationException(AGE_OVER_LIMIT);
        }
        createEmployeeDTO.setAge(ageStr);
        log.info("Validation successful for create employee input");

        return createEmployeeDTO;
    }
}