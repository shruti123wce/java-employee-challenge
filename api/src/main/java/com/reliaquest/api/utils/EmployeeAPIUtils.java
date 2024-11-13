package com.reliaquest.api.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.entity.*;
import com.reliaquest.api.exceptions.ErrorCode;
import com.reliaquest.api.exceptions.InternalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

//log info statement change
//naming conventions change
// add log statements in case of throw new throw new ValidationException(ErrorCode.MISSING_ID);

@Slf4j
@Component
public class EmployeeAPIUtils {
    private static final String BASE_URL = "http://localhost:8112/api/v1/employee";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public EmployeeAPIUtils(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }


    public List<Employee> getAllEmployees() {
        try {
            String externalUrl =  BASE_URL;
            log.info("calling external api at to get all employees "  + externalUrl );
            String response = restTemplate.getForObject(externalUrl, String.class);
            log.info("Successfully fetched response from api: {}", response);

            EmployeeResponses employeeResponse = objectMapper.readValue(response, EmployeeResponses.class);
            log.info("Successfully parsed response: {}", employeeResponse);

            return employeeResponse.getData();
        }
        catch (RestClientException e) {
            log.error("Error while fetching all employees " + e.getMessage(), e);
            throw new InternalException(ErrorCode.API_REQUEST_FAILURE);
        } catch (JsonProcessingException e) {
            log.error("Error while parsing employees response " + e.getMessage(), e);
            throw new InternalException(ErrorCode.JSON_DESERIALIZATION_FAILURE);
        }
    }

    public Employee getEmployeeById(String id) {
        try {
            String externalUrl =  BASE_URL+"/" + id;
            log.info("calling external api at to get all employees "  + externalUrl );
            String response = restTemplate.getForObject(externalUrl, String.class);
            log.info("Successfully fetched response from api: {}", response);

            EmployeeResponse employeeResponse = objectMapper.readValue(response, EmployeeResponse.class);
            log.info("Successfully parsed response: {}", employeeResponse);

            return employeeResponse.getData();
        } catch (RestClientException e) {
            log.error("Error while fetching all employees " + e.getMessage(), e);
            throw new InternalException(ErrorCode.API_REQUEST_FAILURE);
        } catch (JsonProcessingException e) {
            log.error("Error while parsing employees response " + e.getMessage(), e);
            throw new InternalException(ErrorCode.JSON_DESERIALIZATION_FAILURE);
        }
    }

    public CreateEmployee createEmployee(CreateEmployeeDTO createEmployeeDTO) {
        try {
            String externalUrl =  BASE_URL + "/create";
            log.info("calling external api at to get all employees "  + externalUrl );
            String response = restTemplate.postForObject(externalUrl, createEmployeeDTO, String.class);
            log.info("Successfully fetched response from api: {}", response);

            CreateEmployeeResponse createEmployeeResponse = objectMapper.readValue(response, CreateEmployeeResponse.class);
            log.info("Successfully parsed response: {}", createEmployeeResponse);

            return createEmployeeResponse.getData();
        } catch (RestClientException e) {
            log.error("Error while fetching all employees " + e.getMessage(), e);
            throw new InternalException(ErrorCode.API_REQUEST_FAILURE);
        } catch (JsonProcessingException e) {
            log.error("Error while parsing employees response " + e.getMessage(), e);
            throw new InternalException(ErrorCode.JSON_DESERIALIZATION_FAILURE);
        }
    }

    public String deleteEmployee(Employee employeeToBeDeleted) {
        try {
            String id = employeeToBeDeleted.getId();
            String externalUrl = BASE_URL + "/delete/" + id;
            String response = restTemplate.exchange(externalUrl, HttpMethod.DELETE, null, String.class).getBody();
            return response.contains("successfully! deleted Record") ? employeeToBeDeleted.getName() : "failed";
        }  catch (RestClientException e) {
            log.error("Error while fetching all employees " + e.getMessage(), e);
            throw new InternalException(ErrorCode.API_REQUEST_FAILURE);
        }
    }
}