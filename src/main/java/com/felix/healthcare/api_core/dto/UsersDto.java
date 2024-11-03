package com.felix.healthcare.api_core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

public class UsersDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaveRequest{
        private String username;
        private String password;
        private String email;
        private Set<String> rolesName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaveResponse{
        private String username;
        private String email;
        private Set<String> rolesName;
    }

}
