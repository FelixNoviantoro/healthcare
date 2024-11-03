package com.felix.healthcare.api_core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class LabResultDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubmitRequest{
        private Long patientId;
        private String testType;
        private String sampleData;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubmitResponse{
        private String taskId;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StatusResponse{
        private String taskId;
        private String status;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BulkUploadResponse{
        private String jobId;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BulkUploadSummaryResponse{
        private List<BulkUploadResponse> bulkUploadResponseList;
        private Integer records;
        private Integer errors;
    }

}
