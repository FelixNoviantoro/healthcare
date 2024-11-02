package com.felix.healthcare.api_core.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "lab_result")
public class LabResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", referencedColumnName = "id")
    private Patient patientId;
    private String testType;
    private String sampleData;
    private String status;
    private String resultData;
    private String taskId;

    @CreationTimestamp
    private LocalDateTime dateCreated;
}
