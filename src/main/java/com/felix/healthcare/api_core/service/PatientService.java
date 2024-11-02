package com.felix.healthcare.api_core.service;

import com.felix.healthcare.api_core.dto.PatientDto;
import com.felix.healthcare.api_core.entity.Patient;
import com.felix.healthcare.api_core.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    public List<Patient> getAll(){
        return patientRepository.findAll();
    }

    public Patient save(PatientDto.Save data){
        Patient patient = new Patient();
        patient.setName(data.getName());

        return patientRepository.save(patient);
    }

}
