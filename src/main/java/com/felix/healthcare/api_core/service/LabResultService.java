package com.felix.healthcare.api_core.service;

import com.felix.healthcare.api_core.dto.LabResultDto;
import com.felix.healthcare.api_core.entity.LabResult;
import com.felix.healthcare.api_core.entity.Patient;
import com.felix.healthcare.api_core.repository.LabResultRepository;
import com.felix.healthcare.api_core.repository.PatientRepository;
import com.felix.healthcare.api_core.utils.RandomIdGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class LabResultService {

    private final LabResultRepository labResultRepository;
    private final PatientRepository patientRepository;
    private final CacheManager cacheManager;

    public List<LabResult> getAll(){
        List<LabResult> labResults = labResultRepository.findAll();
        return labResults;
    }

    public Optional<LabResult> getByTaskId(String taskId){
        return labResultRepository.findByTaskId(taskId);
    }

    public List<LabResult> getByJobId(String jobId){
        return labResultRepository.findByJobId(jobId);
    }

    public String submit(LabResultDto.SubmitRequest data){
        Optional<Patient> patient = patientRepository.findById(data.getPatientId());

        if (patient.isEmpty()){
            log.info("patien dengan id " + data.getPatientId() + " tidak ditemukan!");
            return "-";
        }

        // Generate Task Id
        String taskId = RandomIdGenerator.randomGenerator("Task-");

        LabResult labResult = new LabResult();
        labResult.setPatientId(patient.get());
        labResult.setTestType(data.getTestType());
        labResult.setSampleData(data.getSampleData());
        labResult.setResultData("test");
        labResult.setStatus("Pending");
        labResult.setTaskId(taskId);

        labResultRepository.save(labResult);

        return taskId;
    }

    @Async
    public CompletableFuture<LabResultDto.BulkUploadSummaryResponse> saveFromExcel(byte[] fileBytes, String jobId) throws IOException {

        Cache jobStatusCache = cacheManager.getCache("jobStatusCache");
        jobStatusCache.put(jobId, "IN_PROGRESS");

        CompletableFuture<LabResultDto.BulkUploadSummaryResponse> task = new CompletableFuture<>();
        try {
            InputStream inputStream = new ByteArrayInputStream(fileBytes);
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

            // Let say we use the first sheet
            XSSFSheet sheet = workbook.getSheetAt(0);

            DataFormatter dataFormatter = new DataFormatter();

            // Instantiate the response model

            List<LabResultDto.BulkUploadResponse> bulkUploadResponses = new ArrayList<>();
            LabResultDto.BulkUploadSummaryResponse summaryResponse = new LabResultDto.BulkUploadSummaryResponse();

            int records = 0;
            int errors = 0;
            int index = 0;

            for (Row row: sheet){
                // Check if the row is header
                if (index++ == 0) continue;
                boolean isFailed = false;
                //records counter
                records++;

                // Prepare labresult object for data save
                LabResult labResult = new LabResult();

                // Generate task id
                String taskId = RandomIdGenerator.randomGenerator("Task-");

                // Check if the row is not null and the type is numeric for the patient id
                if (row.getCell(0) != null && row.getCell(0).getCellType() == CellType.NUMERIC){
                    // Get the excel numeric value
                    double idDouble = row.getCell(0).getNumericCellValue();

                    // Check if the patient exist
                    Optional<Patient> patient = patientRepository.findById((long) idDouble);

                    // Set the patient if present and go to the next row if the patient empty
                    if(patient.isPresent()){
                        labResult.setPatientId(patient.get());
                    } else {
                        // Count the error record and set the status to failed
                        isFailed = true;
                    }
                }

                // Check for the test type data and format this to string
                if (!isFailed && row.getCell(1) != null) {
                    labResult.setTestType(dataFormatter.formatCellValue(row.getCell(1)));
                } else{
                    // Count the error record and set the status to failed
                    isFailed = true;
                }

                // Check for the sample data and format this to string
                if (!isFailed && row.getCell(2) != null) {
                    labResult.setSampleData(dataFormatter.formatCellValue(row.getCell(2)));
                } else {
                    isFailed = true;
                }

                labResult.setTaskId(taskId);
                labResult.setJobId(jobId);

                if (isFailed){
                    // Count the error record and set the status to failed
                    errors++;
                    labResult.setStatus("Failed");
                    labResult.setResultData("-");
                } else {
                    // Success record object
                    labResult.setStatus("Pending");
                    labResult.setResultData("result");
                }

                // Save the lab result
                labResultRepository.save(labResult);
            }

            // Set the summary response
            summaryResponse.setErrors(errors);
            summaryResponse.setRecords(records);

            task.complete(summaryResponse);
            jobStatusCache.put(jobId, "COMPLETED");

        } catch (Exception e) {

            log.error("An error occurred: ", e);
            task.completeExceptionally(e);

        }

        return task;
    }

}
