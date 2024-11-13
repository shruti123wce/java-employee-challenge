package com.reliaquest.api.controller;

import com.reliaquest.api.entity.CreateEmployee;
import com.reliaquest.api.entity.Employee;
import com.reliaquest.api.services.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/employee")
public class EmployeeControllerImplementation implements IEmployeeController<Employee, CreateEmployee> {
    private final EmployeeService employeeService;

    public EmployeeControllerImplementation(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        log.info("calling api to get all employees ");
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/search/{searchString}")
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(@PathVariable String searchString) {
        log.info("calling api to get all employees with name " + searchString);
        return ResponseEntity.ok(employeeService.getEmployeesByNameSearch(searchString));
    }

    @GetMapping("/highest-salary")
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        log.info("calling api to get highest salaries of employees");
        return ResponseEntity.ok(employeeService.getHighestSalaryOfEmployees());
    }

    @Override
    @GetMapping("/top-10-highest-earning")
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        log.info("calling api to get top 10 highest earning employees");
        return ResponseEntity.ok(employeeService.getTopTenHighestEarningEmployeeNames());
    }


    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable String id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }


    @PostMapping()
    public ResponseEntity<Employee> createEmployee(@RequestBody CreateEmployee employeeInput) {
        Map<String, Object> employee= Map.of("name", employeeInput.getName(), "salary", employeeInput.getSalary(), "age", employeeInput.getAge(),"title",employeeInput.getTitle(),"email",employeeInput.getEmail());
        Employee createdEmployee = employeeService.createEmployee(employee);
        return ResponseEntity.ok(createdEmployee);
    }


    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployeeById(@PathVariable String id) {
        return ResponseEntity.ok(employeeService.deleteEmployee(id));
    }
}