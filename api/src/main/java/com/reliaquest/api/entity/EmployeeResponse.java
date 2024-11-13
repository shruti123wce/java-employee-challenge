package com.reliaquest.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class EmployeeResponse {
    private Employee data;

    public Employee getData() {
        return data;
    }
}