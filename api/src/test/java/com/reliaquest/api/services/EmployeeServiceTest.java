package com.reliaquest.api.services;

import com.reliaquest.api.entity.*;
import com.reliaquest.api.exceptions.EmployeeException;
import com.reliaquest.api.exceptions.ErrorCode;
import com.reliaquest.api.exceptions.ValidationException;
import com.reliaquest.api.utils.EmployeeAPIUtils;
import com.reliaquest.api.validator.InputValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    private EmployeeAPIUtils employeeAPIUtils;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void retrieveAllEmployees_WhenDataIsAvailable_ShouldReturnEmployeesList() {
        Employee alice = new Employee("1", "Alice Brown", "50000", "21","Software Engineer","alicebrown@reliaquest.com");
        Employee bob = new Employee("2", "Bob Smith", "75000", "40", "Senior Software Engineer", "bobsmith@reliaquest.com");
        EmployeeResponses employeeResponse = new EmployeeResponses( List.of(alice, bob));
        when(employeeAPIUtils.getAllEmployees()).thenReturn(employeeResponse.getData());

        List<Employee> employees = employeeService.getAllEmployees();

        assertNotNull(employees);
        assertEquals(2, employees.size());
        assertEquals(alice, employees.get(0));
        assertEquals(bob, employees.get(1));
    }

    @Test
    void retrieveAllEmployees_WhenNoDataFound_ShouldThrowException() {
        EmployeeResponses employeeResponse = new EmployeeResponses(List.of());
        when(employeeAPIUtils.getAllEmployees()).thenReturn(employeeResponse.getData());

        EmployeeException exception = assertThrows(EmployeeException.class, () -> {
            employeeService.getAllEmployees();
        });
        System.out.println(exception.getMessage());
        assertEquals("Employee data not found", exception.getMessage());
        assertEquals("ERR-201", exception.getError().getCode());
    }

    @Test
    void searchEmployeesByName_WhenMatchingNameExists_ShouldReturnFilteredResults() {
        Employee alice = new Employee("1", "Alice Brown", "55000", "30","Senior Manager", "alicebrown@reliaquest.com");
        Employee bob = new Employee("2", "Bob Smith", "75000", "40","Senior Software Engineer", "bobsmith@reliaquest.com");
        EmployeeResponses employeeResponse = new EmployeeResponses( List.of(alice, bob));
        when(employeeAPIUtils.getAllEmployees()).thenReturn(employeeResponse.getData());

        List<Employee> employeesWithNameAlice = employeeService.getEmployeesByNameSearch("Alice");

        assertEquals(1, employeesWithNameAlice.size());
        assertEquals(alice, employeesWithNameAlice.get(0));
    }

    @Test
    void searchEmployeesByName_WhenMultipleMatchesFound_ShouldReturnAllMatches() {
        Employee alice = new Employee("1", "Alice Brown", "55000", "29","Software Engineer","alicebrown@reliaquest.com");
        Employee bob = new Employee("2", "Bob Smith", "75000", "52","Senior Software Engineer", "bobsmith@reliaquest.com");
        Employee aliceDoe = new Employee("3", "Alice Doe", "60000", "35"," Data Engineer", "alicedoe@reliaquest.com");
        EmployeeResponses employeeResponse = new EmployeeResponses(List.of(alice, bob, aliceDoe));
        when(employeeAPIUtils.getAllEmployees()).thenReturn(employeeResponse.getData());

        List<Employee> employeesWithNameAlice = employeeService.getEmployeesByNameSearch("Alice");

        assertEquals(2, employeesWithNameAlice.size());
        assertEquals(alice, employeesWithNameAlice.get(0));
        assertEquals(aliceDoe, employeesWithNameAlice.get(1));
    }

    @Test
    void searchEmployeesByName_WhenNoMatchesFound_ShouldThrowException() {
        Employee alice = new Employee("1", "Alice Brown", "55000", "29","Software Engineer","alicebrown@reliaquest.com");
        Employee bob = new Employee("2", "Bob Smith", "75000", "52","Senior Software Engineer", "bobsmith@reliaquest.com");
        Employee aliceDoe = new Employee("3", "Alice Doe", "60000", "35"," Data Engineer", "alicedoe@reliaquest.com");
        EmployeeResponses employeeResponse = new EmployeeResponses(List.of(alice, bob, aliceDoe));
        when(employeeAPIUtils.getAllEmployees()).thenReturn(employeeResponse.getData());

        EmployeeException exception = assertThrows(EmployeeException.class, () -> {
            employeeService.getEmployeesByNameSearch("John");
        });
        assertEquals("No matching employees for the provided name", exception.getMessage());
        assertEquals("ERR-202", exception.getError().getCode());
    }

    @Test
    void retrieveHighestSalary_WhenDataExists_ShouldReturnHighestSalary() {
        Employee alice = new Employee("1", "Alice Brown", "55000", "29","Software Engineer","alicebrown@reliaquest.com");
        Employee bob = new Employee("2", "Bob Smith", "75000", "52","Senior Software Engineer", "bobsmith@reliaquest.com");
        Employee charlie = new Employee("3", "Charlie Doe", "60000", "35"," Data Engineer", "charliedoe@reliaquest.com");
        EmployeeResponses employeeResponse = new EmployeeResponses( List.of(alice, bob, charlie));
        when(employeeAPIUtils.getAllEmployees()).thenReturn(employeeResponse.getData());
        Integer expectedHighestSalary = 75000;

        Integer highestSalary = employeeService.getHighestSalaryOfEmployees();

        assertEquals(expectedHighestSalary, highestSalary);
    }

    @Test
    void retrieveTop10HighestEarningEmployees_WhenMoreThan10Exist_ShouldReturnTop10() {
        Employee alice = new Employee("1", "Alice Brown", "60000", "34", "Software Engineer", "alice.brown@reliaquest.com");
        Employee bob = new Employee("2", "Bob Smith", "55000", "36", "Senior Developer", "bob.smith@reliaquest.com");
        Employee charlie = new Employee("3", "Charlie Green", "52000", "31", "Product Manager", "charlie.green@reliaquest.com");
        Employee daniel = new Employee("4", "Daniel Blue", "51000", "33", "Designer", "daniel.blue@reliaquest.com");
        Employee eva = new Employee("5", "Eva White", "50000", "26", "Software Engineer", "eva.white@reliaquest.com");
        Employee frank = new Employee("6", "Frank Black", "58000", "30", "Quality Assurance", "frank.black@reliaquest.com");
        Employee george = new Employee("7", "George Gray", "57000", "35", "DevOps Engineer", "george.gray@reliaquest.com");
        Employee helen = new Employee("8", "Helen Red", "54000", "37", "Scrum Master", "helen.red@reliaquest.com");
        Employee isaac = new Employee("9", "Isaac Yellow", "53000", "34", "Data Analyst", "isaac.yellow@reliaquest.com");
        Employee jacob = new Employee("10", "Jacob Purple", "60000", "30", "Team Lead", "jacob.purple@reliaquest.com");
        Employee karen = new Employee("11", "Karen Silver", "51000", "34", "Backend Developer", "karen.silver@reliaquest.com");
        Employee laura = new Employee("12", "Laura Gold", "52000", "34", "Frontend Developer", "laura.gold@reliaquest.com");


        EmployeeResponses employeeResponse = new EmployeeResponses(List.of(alice, bob, charlie, daniel, eva, frank, george, helen, isaac, jacob, karen, laura));
        List<String> expectedTop10HighestEarningEmployees = List.of(
                "Alice Brown", "Jacob Purple", "Frank Black", "George Gray", "Bob Smith",
                "Helen Red", "Isaac Yellow", "Charlie Green", "Laura Gold", "Daniel Blue"
        );
        when(employeeAPIUtils.getAllEmployees()).thenReturn(employeeResponse.getData());

        List<String> top10HighestEarningEmployeeNames = employeeService.getTopTenHighestEarningEmployeeNames();

        assertEquals(10, top10HighestEarningEmployeeNames.size());
        assertEquals(expectedTop10HighestEarningEmployees, top10HighestEarningEmployeeNames);
    }

    @Test
    void retrieveEmployeeById_WhenIdMatches_ShouldReturnEmployee() {
        Employee alice = new Employee("1", "Alice Brown", "55000", "29","Software Engineer","alicebrown@reliaquest.com");
        EmployeeResponse employeeResponse = new EmployeeResponse( alice);
        when(employeeAPIUtils.getEmployeeById("1")).thenReturn(employeeResponse.getData());

        Employee employee = employeeService.getEmployeeById("1");

        assertEquals(alice, employee);
    }

    @Test
    void retrieveEmployeeById_WhenNoMatchFound_ShouldThrowException() {
        EmployeeException exception = assertThrows(EmployeeException.class, () -> {
            employeeService.getEmployeeById("10");
        });
        assertEquals("Employee data not found", exception.getMessage());
        assertEquals("ERR-201", exception.getError().getCode());
    }

    @Test
    void retrieveEmployeeById_WhenIdIsNull_ShouldThrowValidationException() {
        String id = null;

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            employeeService.getEmployeeById(id);
        });

        assertEquals(ErrorCode.MISSING_ID.getMessage(), exception.getError().getMessage());
    }

    @Test
    void addNewEmployee_WithValidData_ShouldReturnCreatedEmployee() {
        Map<String, Object> employeeInput = new HashMap<>();
        employeeInput.put("name", "Alice Brown");
        employeeInput.put("salary", "50000");
        employeeInput.put("age", "29");
        employeeInput.put("title", "Software Engineer");
        employeeInput.put("email", "alicebrown@reliaquest.com");

        Employee newEmployee = new Employee("1", "Alice Brown", "50000", "29","Software Engineer","alicebrown@reliaquest.com");
        CreateEmployeeDTO createEmployeeDTO = InputValidator.convertAndValidateEmployeeInput(employeeInput);
        CreateEmployee createEmployee = new CreateEmployee("1", "Alice Brown", "50000", "29", "Software Engineer", "alicebrown@reliaquest.com");
        when(employeeAPIUtils.createEmployee(createEmployeeDTO)).thenReturn(createEmployee);

        Employee createdEmployee = employeeService.createEmployee(employeeInput);

        assertNotNull(createdEmployee);
        assertEquals(newEmployee, createdEmployee);
    }

    @Test
    void deleteEmployeeById_WithValidId_ShouldReturnDeletedEmployeeName() {
        Employee alice = new Employee("1", "Alice Brown", "55000", "29","Software Engineer","alicebrown@reliaquest.com");
        EmployeeResponses employeeResponse = new EmployeeResponses( List.of(alice));
        when(employeeAPIUtils.getEmployeeById("1")).thenReturn(employeeResponse.getData().get(0));
        when(employeeAPIUtils.deleteEmployee(alice)).thenReturn(alice.getName());

        String deletedEmployeeName = employeeService.deleteEmployee("1");

        assertEquals("Alice Brown", deletedEmployeeName);
    }

    @Test
    void deleteEmployee_IdIsNull() {
        String id = null;

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            employeeService.deleteEmployee(id);
        });

        assertEquals(ErrorCode.MISSING_ID.getMessage(), exception.getError().getMessage());
    }
}
