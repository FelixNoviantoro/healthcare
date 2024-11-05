package com.felix.healthcare.api_core.service;

import com.felix.healthcare.api_core.dto.PatientDto;
import com.felix.healthcare.api_core.entity.Patient;
import com.felix.healthcare.api_core.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    private PatientDto.Save saveRequest;

    @BeforeEach
    void setUp() {
        // Set up a valid save request
        saveRequest = new PatientDto.Save();
        saveRequest.setName("patient-1");
    }

    @Test
    void getAll_shouldReturnAllPatients() {
        // Arrange
        Patient patient1 = new Patient();
        patient1.setName("patient-1");

        Patient patient2 = new Patient();
        patient2.setName("patient-2");

        when(patientRepository.findAll()).thenReturn(Arrays.asList(patient1, patient2));

        // Act
        List<Patient> patients = patientService.getAll();

        // Assert
        assertNotNull(patients);
        assertEquals(2, patients.size());
        assertEquals("patient-1", patients.get(0).getName());
        assertEquals("patient-2", patients.get(1).getName());

        // Verify interactions
        verify(patientRepository).findAll();
    }

    @Test
    void save_shouldSavePatient() {
        // Arrange
        Patient patient = new Patient();
        patient.setName(saveRequest.getName());

        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Patient savedPatient = patientService.save(saveRequest);

        // Assert
        assertNotNull(savedPatient);
        assertEquals("patient-1", savedPatient.getName());

        // Verify interactions
        verify(patientRepository).save(any(Patient.class));
    }
}