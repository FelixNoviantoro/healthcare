package com.felix.healthcare.api_core.repository;

import com.felix.healthcare.api_core.entity.LabResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LabResultRepository extends JpaRepository<LabResult, Long> {
    Optional<LabResult> findByTaskId(String taskId);
    List<LabResult> findByJobId(String jobId);
}
