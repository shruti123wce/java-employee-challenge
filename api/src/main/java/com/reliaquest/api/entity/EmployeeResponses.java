package com.reliaquest.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class EmployeeResponses {
    private List<Employee> data;

    public List<Employee> getData() {
        return data;
    }
}