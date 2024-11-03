package com.felix.healthcare.api_core.controller;

import com.felix.healthcare.api_core.dto.BaseResponse;
import com.felix.healthcare.api_core.dto.LabResultDto;
import com.felix.healthcare.api_core.entity.LabResult;
import com.felix.healthcare.api_core.service.LabResultService;
import com.felix.healthcare.api_core.utils.RandomIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/lab-result")
@RequiredArgsConstructor
@Slf4j
public class LabResultController {

    private final LabResultService labResultService;
    private final CacheManager cacheManager;

    @GetMapping
    public ResponseEntity<?> getAll(){
        List<LabResult> labResults = labResultService.getAll();
        return ResponseEntity.ok(labResults);
    }

    @PostMapping
    public ResponseEntity<?> submitLabResult(
            @RequestBody LabResultDto.SubmitRequest data
            ){

        String taskId = labResultService.submit(data);

        LabResultDto.SubmitResponse response = new LabResultDto.SubmitResponse();
        response.setTaskId(taskId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> readExcel(
            @RequestPart(name = "file") MultipartFile file
    ) throws IOException {

        byte[] fileBytes = file.getBytes();
        String jobId = RandomIdGenerator.randomGenerator("Job-");
        CompletableFuture<LabResultDto.BulkUploadSummaryResponse> summaryResponse = labResultService.saveFromExcel(fileBytes, jobId);

        summaryResponse.thenAccept(response -> {
            log.info("berhasil dengan repsponse records : " + response.getRecords() + " errors : " + response.getErrors());
        });

        // Set response object
        BaseResponse<LabResultDto.BulkUploadResponse> response = new BaseResponse<>();
        LabResultDto.BulkUploadResponse bulkUploadResponse = new LabResultDto.BulkUploadResponse(jobId);
        response.setStatus(true);
        response.setResponseCode(202);
        response.setMessage("Upload initiated. Please check the status later");
        response.setResult(bulkUploadResponse);

        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @GetMapping("/{taskId}/status")
    public ResponseEntity<?> labResultStatus(
            @PathVariable("taskId") String taskId
    ){
        Optional<LabResult> labResultOptional = labResultService.getByTaskId(taskId);
        BaseResponse<LabResultDto.StatusResponse> response = new BaseResponse<>();

        if (labResultOptional.isPresent()){
            LabResult labResult = labResultOptional.get();

            LabResultDto.StatusResponse statusResponse = new LabResultDto.StatusResponse();
            statusResponse.setStatus(labResult.getStatus());
            statusResponse.setTaskId(labResult.getTaskId());

            // Set response data
            response.setStatus(true);
            response.setMessage("Successfull");
            response.setResponseCode(200);
            response.setResult(statusResponse);

        } else {
            // Set response data
            response.setStatus(true);
            response.setMessage("Successfull");
            response.setResponseCode(200);
            response.setResult(null);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<?> findByTaskId(
            @PathVariable("taskId") String taskId
    ){
        Optional<LabResult> labResultOptional = labResultService.getByTaskId(taskId);
        BaseResponse<LabResult> response = new BaseResponse<>();

        if (labResultOptional.isPresent()){
            LabResult labResult = labResultOptional.get();

            // Set response data
            response.setStatus(true);
            response.setMessage("Successfull");
            response.setResponseCode(200);
            response.setResult(labResult);

        } else {
            // Set response data
            response.setStatus(true);
            response.setMessage("Successfull");
            response.setResponseCode(200);
            response.setResult(null);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/upload/{jobId}")
    public ResponseEntity<?> findByJobId(
            @PathVariable("jobId") String jobId
    ){
        Cache jobStatusCache = cacheManager.getCache("jobStatusCache");
        Cache.ValueWrapper status = jobStatusCache.get(jobId);

        log.info("Job Status : " + status.get().toString());

        if ("IN_PROGRESS".equals(status.get().toString())){
            BaseResponse<String> response = new BaseResponse<>();

            // Set response data
            response.setStatus(true);
            response.setMessage("on process");
            response.setResponseCode(200);
            response.setResult("");

            return ResponseEntity.ok(response);

        } else {
            List<LabResult> labResultList = labResultService.getByJobId(jobId);
            BaseResponse<List<LabResult>> response = new BaseResponse<>();

            // Set response data
            response.setStatus(true);
            response.setMessage("Successfull");
            response.setResponseCode(200);
            response.setResult(labResultList);

            return ResponseEntity.ok(response);
        }

    }
}
