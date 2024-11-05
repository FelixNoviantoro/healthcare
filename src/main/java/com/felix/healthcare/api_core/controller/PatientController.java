package com.felix.healthcare.api_core.controller;

import com.felix.healthcare.api_core.dto.BaseResponse;
import com.felix.healthcare.api_core.dto.PatientDto;
import com.felix.healthcare.api_core.entity.Patient;
import com.felix.healthcare.api_core.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patient")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @GetMapping
    public ResponseEntity<?> getAll(){
        List<Patient> patients = patientService.getAll();
        return ResponseEntity.ok(patients);
    }

    @PostMapping
    public ResponseEntity<?> save(
            @RequestBody PatientDto.Save data
            ) {
        Patient patient = patientService.save(data);
        BaseResponse<Patient> response = new BaseResponse<>();
        response.setStatus(true);
        response.setResponseCode(200);
        response.setMessage("Successfull");
        response.setResult(patient);
        return ResponseEntity.ok(response);
    }
}
