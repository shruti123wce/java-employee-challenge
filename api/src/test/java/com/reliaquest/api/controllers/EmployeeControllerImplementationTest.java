package com.reliaquest.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.controller.EmployeeControllerImplementation;
import com.reliaquest.api.entity.CreateEmployee;
import com.reliaquest.api.entity.Employee;
import com.reliaquest.api.exceptions.EmployeeException;
import com.reliaquest.api.exceptions.ErrorCode;
import com.reliaquest.api.exceptions.InternalException;
import com.reliaquest.api.exceptions.ValidationException;
import com.reliaquest.api.services.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;


import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(EmployeeControllerImplementation.class)
public class EmployeeControllerImplementationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    private List<Employee> employees;

    private Employee employee;
    private final String baseUrl = "http://localhost:8112/api/v1/employee";

    @BeforeEach
    void setUp() {
        employees = Arrays.asList(
              new Employee("1", "Alice Brown", "50000", "21","Software Engineer","alicebrown@reliaquest.com"),
              new Employee("2", "Bob Smith", "75000", "40", "Senior Software Engineer", "bobsmith@reliaquest.com")

        );
        employee = new Employee("1", "Alice Brown", "50000", "21","Software Engineer","alicebrown@reliaquest.com");
    }

    @Test
    void getAllEmployees_Success() throws Exception {
        when(employeeService.getAllEmployees()).thenReturn(employees);

        String getAllEmployeesUrl = baseUrl ;
        mockMvc.perform(get(getAllEmployeesUrl)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(employees)));
    }

    @Test
    void getAllEmployees_RestClientError() throws Exception {
        when(employeeService.getAllEmployees()).thenThrow(new InternalException(ErrorCode.API_REQUEST_FAILURE));

        String getAllEmployeesUrl = baseUrl ;
        mockMvc.perform(get(getAllEmployeesUrl)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\":\"ERR-101\",\"message\":\"REST API request execution failed\"}"));
    }

    @Test
    void getAllEmployees_JsonParsingError() throws Exception {
        when(employeeService.getAllEmployees()).thenThrow(new InternalException(ErrorCode.JSON_DESERIALIZATION_FAILURE));

        String getAllEmployeesUrl = baseUrl ;
        mockMvc.perform(get(getAllEmployeesUrl)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\":\"ERR-102\",\"message\":\"Failed to parse JSON data\"}"));
    }

    @Test
    void getAllEmployees_EmployeeError() throws Exception {
        when(employeeService.getAllEmployees()).thenThrow(new EmployeeException(ErrorCode.EMPLOYEE_NAME_NOT_FOUND));

        String getAllEmployeesUrl = baseUrl ;
        mockMvc.perform(get(getAllEmployeesUrl)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\":\"ERR-202\",\"message\":\"No matching employees for the provided name\"}"));
    }

    @Test
    void getAllEmployees_Exception() throws Exception {
        when(employeeService.getAllEmployees()).thenThrow(new RuntimeException("Internal Server Error"));

        String getAllEmployeesUrl = baseUrl ;
        mockMvc.perform(get(getAllEmployeesUrl)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json("{\"error\":\"GENERAL_ERROR\",\"message\":\"An unexpected error occurred\"}"));
    }

    @Test
    void getEmployeesByNameSearch_Success() throws Exception {
        employees = Arrays.asList(
                new Employee("1", "Alice Brown", "50000", "21","Software Engineer","alicebrown@reliaquest.com"),
                new Employee("2", "Bob Smith", "75000", "40", "Senior Software Engineer", "bobsmith@reliaquest.com")
        );
        when(employeeService.getEmployeesByNameSearch("Alice")).thenReturn(employees);
        String employeeName = "Alice";

        String getAllEmployeesUrl = baseUrl + "/search/{searchString}";
        mockMvc.perform(get(getAllEmployeesUrl, employeeName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(employees)));
    }

    @Test
    void getHighestSalaryOfEmployees() throws Exception {
        Integer highestSalaryOfEmployees = 70000;
        when(employeeService.getHighestSalaryOfEmployees()).thenReturn(highestSalaryOfEmployees);

        String getAllEmployeesUrl = baseUrl + "/highest-salary";
        mockMvc.perform(get(getAllEmployeesUrl)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(highestSalaryOfEmployees)));
    }

    @Test
    void getTopTenHighestEarningEmployeeNames_Success() throws Exception {
        List<String> expectedTop10HighestEarningEmployeeNames = List.of(
                "Virat Kohli",
                "Jasprit Bumrah",
                "Hardik Pandya",
                "Ravindra Jadeja",
                "Rohit Sharma",
                "Ravichandran Ashwin",
                "Bhuvneshwar Kumar",
                "KL Rahul",
                "Mohammed Shami",
                "Suryakumar Yadav"
        );
        when(employeeService.getTopTenHighestEarningEmployeeNames()).thenReturn(expectedTop10HighestEarningEmployeeNames);

        String getAllEmployeesUrl = baseUrl + "/top-10-highest-earning";
        mockMvc.perform(get(getAllEmployeesUrl)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedTop10HighestEarningEmployeeNames)));
    }

    @Test
    void getEmployeeById_Success() throws Exception {
        when(employeeService.getEmployeeById("1")).thenReturn(employee);

        String getEmployeeByIdUrl = baseUrl + "/1";
        mockMvc.perform(get(getEmployeeByIdUrl)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(employee)));
    }

    @Test
    void addEmployee_Success() throws Exception {

        CreateEmployee employeeInput = new CreateEmployee("1", "Alice Brown", "50000", "21","Software Engineer","alicebrown@reliaquest.com");

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(employeeInput);

        Employee employee = new Employee("1", "Alice Brown", "50000", "21","Software Engineer","alicebrown@reliaquest.com");

        when(employeeService.createEmployee(anyMap())).thenReturn(employee);

        String createEmployeeUrl = baseUrl;
        mockMvc.perform(post(createEmployeeUrl)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(employee)));
    }

    @Test
    void addEmployee_ValidationException() throws Exception {
        CreateEmployee employeeInput = new CreateEmployee("1", "Alice Brown", "50000", "21","Software Engineer","alicebrown@reliaquest.com");

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(employeeInput);

        Employee employee = new Employee("1", "Alice Brown", "50000", "21","Software Engineer","alicebrown@reliaquest.com");

        when(employeeService.createEmployee(anyMap())).thenThrow(new ValidationException(ErrorCode.INVALID_OR_EMPTY_NAME));


        String createEmployeeUrl = baseUrl;
        mockMvc.perform(post(createEmployeeUrl)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\":\"ERR-302\",\"message\":\"Name is either empty or invalid\"}"));
    }

    @Test
    void deleteEmployeeById_Success() throws Exception {
        String id = "1";
        String employeeName = "John Doe";

        when(employeeService.deleteEmployee(id)).thenReturn(employeeName);

        String deleteEmployeeUrl = baseUrl + "/{id}";
        mockMvc.perform(delete(deleteEmployeeUrl, id))
                .andExpect(status().isOk())
                .andExpect(content().string(employeeName));
    }

}
