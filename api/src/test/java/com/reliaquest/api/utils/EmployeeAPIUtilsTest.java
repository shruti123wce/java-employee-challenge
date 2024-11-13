package com.reliaquest.api.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.entity.*;
import com.reliaquest.api.exceptions.EmployeeException;
import com.reliaquest.api.exceptions.ErrorCode;
import com.reliaquest.api.exceptions.InternalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static jdk.dynalink.linker.support.Guards.isNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeAPIUtilsTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private EmployeeAPIUtils employeeAPI;

    private static final String BASE_URL = "http://localhost:8112/api/v1/employee";

    private String allEmployeesJson;
    private String employeeJson;

    private final String getEmployeesUrl = BASE_URL;
    private final String getEmployeeByIdUrl = BASE_URL;
    private final String createEmployeeUrl = BASE_URL + "/create";

    @BeforeEach
    void setUp() {
        allEmployeesJson = "{  \"data\": [" +
                "{ \"id\": \"1\", \"employee_name\": \"Alice Brown\", \"employee_salary\": \"50000\", \"employee_age\": \"30\", \"employee_title\": \"Software Engineer\",\"employee_email\": \"alice.brown@reliaquest.com\" }," +
                "{ \"id\": \"2\", \"employee_name\": \"Joe Puth \", \"employee_salary\": \"70000\", \"employee_age\": \"40\", \"employee_title\": \"Product Engineer\",\"employee_email\": \"joe.puth@reliaquest.com\"}" +
                "] }";

        employeeJson = "{ \"data\": { \"id\": \"1\", \"employee_name\": \"Alice Brown\", \"employee_salary\": \"50000\", \"employee_age\": \"30\", \"employee_title\": \"Software Engineer\",\"employee_email\": \"alice.brown@reliaquest.com\" } }";
    }

    @Test
    void getAllEmployees_Success() throws JsonProcessingException {
        Employee employee = new Employee("1", "Alice Brown", "50000", "21","Software Engineer","alicebrown@reliaquest.com");

        EmployeeResponses employeeResponse = new EmployeeResponses(List.of(employee));

        when(restTemplate.getForObject(getEmployeesUrl, String.class)).thenReturn(allEmployeesJson);
        when(objectMapper.readValue(allEmployeesJson, EmployeeResponses.class)).thenReturn(employeeResponse);

        List<Employee> employees = employeeAPI.getAllEmployees();

        assertNotNull(employees);
        assertEquals(1, employees.size());
        assertEquals("Alice Brown", employees.get(0).getName());
    }

    @Test
    void getAllEmployees_RestClientException() {
        when(restTemplate.getForObject(getEmployeesUrl, String.class)).thenThrow(new RestClientException("Error"));

        InternalException exception = assertThrows(InternalException.class, () -> {
            employeeAPI.getAllEmployees();
        });

        assertEquals(ErrorCode.API_REQUEST_FAILURE, exception.getError());
        assertEquals("REST API request execution failed", exception.getMessage());
        assertEquals("ERR-101", exception.getError().getCode());
    }

    @Test
    void getAllEmployees_JsonProcessingException() throws JsonProcessingException {
        when(restTemplate.getForObject(getEmployeesUrl, String.class)).thenReturn(allEmployeesJson);
        when(objectMapper.readValue(allEmployeesJson, EmployeeResponses.class)).thenThrow(new JsonProcessingException("Error") {
        });

        InternalException exception = assertThrows(InternalException.class, () -> {
            employeeAPI.getAllEmployees();
        });

        assertEquals(ErrorCode.JSON_DESERIALIZATION_FAILURE, exception.getError());
        assertEquals("Failed to parse JSON data", exception.getMessage());
        assertEquals("ERR-102", exception.getError().getCode());
    }

    @Test
    void getEmployeeById_Success() throws JsonProcessingException {
        Employee employee = new Employee("1", "Alice Brown", "50000", "21","Software Engineer","alicebrown@reliaquest.com");

        EmployeeResponse employeeResponse = new EmployeeResponse( employee);

        when(restTemplate.getForObject(getEmployeeByIdUrl + "/1", String.class)).thenReturn(employeeJson);
        when(objectMapper.readValue(employeeJson, EmployeeResponse.class)).thenReturn(employeeResponse);

        Employee employeeById = employeeAPI.getEmployeeById("1");

        assertNotNull(employeeById);
        assertEquals(employee, employeeById);
    }

    @Test
    void getEmployeeById_RestClientException() {
        when(restTemplate.getForObject(getEmployeeByIdUrl + "/1", String.class)).thenThrow(new RestClientException("Error while fetching record"));

        InternalException exception = assertThrows(InternalException.class, () -> {
            employeeAPI.getEmployeeById("1");
        });

        assertEquals(ErrorCode.API_REQUEST_FAILURE, exception.getError());
        assertEquals("REST API request execution failed", exception.getMessage());
        assertEquals("ERR-101", exception.getError().getCode());
    }

    @Test
    void getEmployeeById_JsonProcessingException() throws JsonProcessingException {
        when(restTemplate.getForObject(getEmployeeByIdUrl + "/1", String.class)).thenReturn(employeeJson);
        when(objectMapper.readValue(employeeJson, EmployeeResponse.class)).thenThrow(new JsonProcessingException("Error"){});

        InternalException exception = assertThrows(InternalException.class, () -> {
            employeeAPI.getEmployeeById("1");
        });

        assertEquals(ErrorCode.JSON_DESERIALIZATION_FAILURE, exception.getError());
        assertEquals("Failed to parse JSON data", exception.getMessage());
        assertEquals("ERR-102", exception.getError().getCode());
    }

    @Test
    void createEmployee_Success() throws JsonProcessingException {
        CreateEmployeeDTO createEmployeeDTO = new CreateEmployeeDTO();
        createEmployeeDTO.setName("Alice Brown");
        createEmployeeDTO.setSalary("50000");
        createEmployeeDTO.setAge("21");
        String jsonResponse = "{\"status\":\"success\",\"data\":{\"name\":\"Alice Brown\",\"salary\":\"50000\",\"age\":\"21\",\"employee_title\": \"Software Engineer\",\"employee_email\": \"alice.brown@reliaquest.com\" }}";
        CreateEmployee createEmployee = new CreateEmployee("1", "Alice Brown", "50000", "21","Software Engineer","alicebrown@reliaquest.com");
        CreateEmployeeResponse createEmployeeResponse = new CreateEmployeeResponse(createEmployee);

        when(restTemplate.postForObject(createEmployeeUrl,createEmployeeDTO, String.class)).thenReturn(jsonResponse);
        when(objectMapper.readValue(jsonResponse,CreateEmployeeResponse.class)).thenReturn(createEmployeeResponse);

        CreateEmployee result = employeeAPI.createEmployee(createEmployeeDTO);

        assertNotNull(result);
        assertEquals("Alice Brown", result.getName());
        assertEquals("50000", result.getSalary());
        assertEquals("21", result.getAge());
    }

    @Test
    void createEmployee_RestClientException() {
        CreateEmployeeDTO createEmployeeDTO = new CreateEmployeeDTO();
        createEmployeeDTO.setName("Alice Brown");
        createEmployeeDTO.setSalary("50000");
        createEmployeeDTO.setAge("30");

        when(restTemplate.postForObject(createEmployeeUrl,createEmployeeDTO, String.class)).thenThrow(new RestClientException("Error"));

        InternalException exception = assertThrows(InternalException.class, () -> {
            employeeAPI.createEmployee(createEmployeeDTO);
        });

        assertEquals(ErrorCode.API_REQUEST_FAILURE, exception.getError());
        assertEquals("REST API request execution failed", exception.getMessage());
        assertEquals("ERR-101", exception.getError().getCode());
    }

    @Test
    void createEmployee_JsonParsingException() throws JsonProcessingException {
        CreateEmployeeDTO createEmployeeDTO = new CreateEmployeeDTO();
        createEmployeeDTO.setName("Alice Brown");
        createEmployeeDTO.setSalary("50000");
        createEmployeeDTO.setAge("30");
        createEmployeeDTO.setTitle("Software Engineer");
        createEmployeeDTO.setEmail("alice.brown@reliaquest.com");

        String jsonResponse = "{\"status\":\"success\",\"data\":{\"name\":\"Alice Brown\",\"salary\":\"50000\",\"age\":\"21\",\"employee_title\": \"Software Engineer\",\"employee_email\": \"alice.brown@reliaquest.com\" }}";

        when(restTemplate.postForObject(createEmployeeUrl,createEmployeeDTO, String.class)).thenReturn(jsonResponse);
        when(objectMapper.readValue(jsonResponse,CreateEmployeeResponse.class)).thenThrow(new JsonProcessingException("Error"){});

        InternalException exception = assertThrows(InternalException.class, () -> {
            employeeAPI.createEmployee(createEmployeeDTO);
        });

        assertEquals(ErrorCode.JSON_DESERIALIZATION_FAILURE, exception.getError());
        assertEquals("Failed to parse JSON data", exception.getMessage());
        assertEquals("ERR-102", exception.getError().getCode());
    }

    @Test
    void deleteEmployee_Success() {
        Employee employee = new Employee("1", "Alice Brown", "50000", "21","Software Engineer","alicebrown@reliaquest.com");
        String jsonResponse = "successfully! deleted Record";

        when(restTemplate.exchange(BASE_URL + "/delete/1", HttpMethod.DELETE, null, String.class)).thenReturn(ResponseEntity.ok(jsonResponse));

        String result = employeeAPI.deleteEmployee(employee);

        assertEquals("Alice Brown", result);
    }

    @Test
    void deleteEmployee_FailureResponse() {
        Employee employee = new Employee("1", "Alice Brown", "50000", "21","Software Engineer","alicebrown@reliaquest.com");
        String jsonResponse = "failed to delete Record";

        when(restTemplate.exchange(BASE_URL + "/delete/1", HttpMethod.DELETE, null, String.class)).thenReturn(ResponseEntity.ok(jsonResponse));

        String result = employeeAPI.deleteEmployee(employee);

        assertEquals("failed", result);
    }

    @Test
    void deleteEmployee_RestClientException() {
        Employee employee = new Employee("1", "Alice Brown", "50000", "21","Software Engineer","alicebrown@reliaquest.com");

        when(restTemplate.exchange(BASE_URL + "/delete/1", HttpMethod.DELETE, null, String.class)).thenThrow(new RestClientException("Error"));
        InternalException exception = assertThrows(InternalException.class, () -> {
            employeeAPI.deleteEmployee(employee);
        });

        assertEquals(ErrorCode.API_REQUEST_FAILURE.getMessage(), exception.getMessage());
    }
}