package com.felix.healthcare.api_core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse<T> {

    private boolean status; // Indicates whether the operation was successful or not
    private Integer responseCode; // A code indicating the status of the response
    private T result; // The result body (generic to handle different types)
    private String message; // Error message if the operation was unsuccessful

}
