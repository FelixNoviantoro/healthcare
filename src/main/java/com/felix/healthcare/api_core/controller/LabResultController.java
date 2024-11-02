package com.felix.healthcare.api_core.controller;

import com.felix.healthcare.api_core.dto.BaseResponse;
import com.felix.healthcare.api_core.dto.LabResultDto;
import com.felix.healthcare.api_core.entity.LabResult;
import com.felix.healthcare.api_core.service.LabResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lab-result")
@RequiredArgsConstructor
public class LabResultController {

    private final LabResultService labResultService;

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

        LabResultDto.BulkUploadSummaryResponse summaryResponse = labResultService.saveFromExcel(file);

        // Set response object
        BaseResponse<LabResultDto.BulkUploadSummaryResponse> response = new BaseResponse<>();
        response.setStatus(true);
        response.setResponseCode(202);
        response.setMessage("Successfull");
        response.setResult(summaryResponse);

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
}
