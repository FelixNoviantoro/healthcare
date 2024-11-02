package com.felix.healthcare.api_core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class PatientDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Save{
        private String name;
    }

}
