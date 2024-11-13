package com.reliaquest.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class CreateEmployeeDTO {

    @JsonProperty("name")
    @JsonIgnore
    private String name;
    @JsonProperty("salary")
    @JsonIgnore
    private String salary;
    @JsonProperty("age")
    @JsonIgnore
    private String age;
    @JsonProperty("title")
    @JsonIgnore
    private String title;
    @JsonIgnore
    @JsonProperty("email")
    private String email;
}