package com.reliaquest.api.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.reliaquest.api.utils.EmployeeAPIUtils;
import com.reliaquest.api.exceptions.ErrorCode;
import com.reliaquest.api.exceptions.EmployeeException;
import com.reliaquest.api.exceptions.ValidationException;
import com.reliaquest.api.entity.CreateEmployee;
import com.reliaquest.api.entity.CreateEmployeeDTO;
import com.reliaquest.api.entity.Employee;
import com.reliaquest.api.validator.InputValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//log info statement change
//naming conventions change
// add log statements in case of throw new throw new ValidationException(ErrorCode.MISSING_ID);

@Service
@Slf4j
public class EmployeeService {

    private final EmployeeAPIUtils employeeAPIUtils;

    public EmployeeService(EmployeeAPIUtils employeeAPI) {
        this.employeeAPIUtils = employeeAPI;
    }

    public List<Employee> getAllEmployees() {
        List<Employee> allEmployees = employeeAPIUtils.getAllEmployees();
        if(allEmployees.size() == 0)
            throw new EmployeeException(ErrorCode.NO_RECORDS_FOUND);
        log.info("List of all Employees is : {}", allEmployees.toString());
        return allEmployees;
    }

    public List<Employee> getEmployeesByNameSearch(String name) {
        List<Employee> employeesFoundByName = getAllEmployees().stream()
                .filter(employee -> employee.getName().contains(name))
                .collect(Collectors.toList());
        if(employeesFoundByName.size()==0)
            throw new EmployeeException(ErrorCode.EMPLOYEE_NAME_NOT_FOUND);
        log.info("Employees with name " + name + " are : {}", employeesFoundByName);
        return employeesFoundByName;
    }

    public int getHighestSalaryOfEmployees() {
        int highestSalaryOfEmployees = getAllEmployees().stream()
                .mapToInt(employee -> Integer.parseInt(employee.getSalary()))
                .max()
                .orElse(0);
        log.info("Highest earning employee salary is: {}", highestSalaryOfEmployees);
        return highestSalaryOfEmployees;
    }

    public List<String> getTopTenHighestEarningEmployeeNames() {
        List<String> top10HighestEarningEmployeeNames = getAllEmployees().stream()
                .sorted((e1, e2) -> Integer.compare(Integer.parseInt(e2.getSalary()), Integer.parseInt(e1.getSalary())))
                .limit(10)
                .map(Employee::getName)
                .collect(Collectors.toList());
        log.info("Top 10 highest earning employee names: {}", top10HighestEarningEmployeeNames);
        return top10HighestEarningEmployeeNames;
    }

    public Employee getEmployeeById(String id) {
        if(id == null)
            throw new ValidationException(ErrorCode.MISSING_ID);
        Employee employee = employeeAPIUtils.getEmployeeById(id);
        if(employee == null)
            throw new EmployeeException(ErrorCode.NO_RECORDS_FOUND);
        log.info("Successfully found Employee with id : {}", employee);
        return employee;
    }
    public Employee createEmployee(Map<String,Object>  employeeInput) {
        CreateEmployeeDTO createEmployeeDTO = InputValidator.convertAndValidateEmployeeInput(employeeInput);
        CreateEmployee createEmployee = employeeAPIUtils.createEmployee(createEmployeeDTO);
        log.info("Successfully created Employee with id : {}", createEmployee);
        return new Employee(createEmployee.getId().toString(), createEmployee.getName(), createEmployee.getSalary(), createEmployee.getAge(), createEmployee.getTitle(), createEmployee.getEmail());
    }

    public String deleteEmployee(String id) {
        if(id == null)
            throw new ValidationException(ErrorCode.MISSING_ID);
        Employee employeeToBeDeleted = getEmployeeById(id);
        employeeAPIUtils.deleteEmployee(employeeToBeDeleted);
        return employeeToBeDeleted.getName();
    }
}