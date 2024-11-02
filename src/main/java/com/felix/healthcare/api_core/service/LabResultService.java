package com.felix.healthcare.api_core.service;

import com.felix.healthcare.api_core.dto.LabResultDto;
import com.felix.healthcare.api_core.entity.LabResult;
import com.felix.healthcare.api_core.entity.Patient;
import com.felix.healthcare.api_core.repository.LabResultRepository;
import com.felix.healthcare.api_core.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class LabResultService {

    private final LabResultRepository labResultRepository;
    private final PatientRepository patientRepository;

    public List<LabResult> getAll(){
        List<LabResult> labResults = labResultRepository.findAll();
        return labResults;
    }

    public Optional<LabResult> getByTaskId(String taskId){
        return labResultRepository.findByTaskId(taskId);
    }

    public String submit(LabResultDto.SubmitRequest data){
        Optional<Patient> patient = patientRepository.findById(data.getPatientId());

        if (patient.isEmpty()){
            log.info("patien dengan id " + data.getPatientId() + " tidak ditemukan!");
            return "-";
        }

        // Generate Task Id
        String taskId = generateTaskId();

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

    public LabResultDto.BulkUploadSummaryResponse saveFromExcel(MultipartFile file) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());

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

            //records counter
            records++;

            // Prepare labresult object for data save
            LabResult labResult = new LabResult();

            // Generate task id
            String taskId = generateTaskId();

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
                    errors++;
                    labResult.setStatus("Failed");
                    bulkUploadResponses.add(labResultToReponseMapper(labResult));

                    // Jump into the next row
                    continue;
                }
            }

            // Check for the test type data and format this to string
            if (row.getCell(1) != null) {
                labResult.setTestType(dataFormatter.formatCellValue(row.getCell(1)));
            } else{
                // Count the error record and set the status to failed
                errors++;
                labResult.setStatus("Failed");
                bulkUploadResponses.add(labResultToReponseMapper(labResult));

                // Jump into the next row
                continue;
            }

            // Check for the sample data and format this to string
            if (row.getCell(2) != null) {
                labResult.setSampleData(dataFormatter.formatCellValue(row.getCell(2)));
            } else {
                // Count the error record and set the status to failed
                errors++;
                labResult.setStatus("Failed");
                bulkUploadResponses.add(labResultToReponseMapper(labResult));

                // Jump into the next row
                continue;
            }

            labResult.setStatus("Pending");
            labResult.setResultData("result");
            labResult.setTaskId(taskId);

            // Save the lab result
            labResultRepository.save(labResult);

            // Add data to response list
            bulkUploadResponses.add(labResultToReponseMapper(labResult));
        }

        // Set the summary response
        summaryResponse.setBulkUploadResponseList(bulkUploadResponses);
        summaryResponse.setErrors(errors);
        summaryResponse.setRecords(records);

        return summaryResponse;
    }

    public static String generateTaskId() {
        Random random = new Random();

        // Generates a random number between 100 and 999 and add 'Task' as prefix
        int randomNumber = 100 + random.nextInt(900);
        return "Task" + randomNumber;
    }

    private LabResultDto.BulkUploadResponse labResultToReponseMapper(LabResult data){
        // Prepare for response object
        LabResultDto.BulkUploadResponse response = new LabResultDto.BulkUploadResponse();

        // Map data to response
        response.setStatus(data.getStatus());
        response.setTaskId(data.getTaskId());

        return response;
    }

}
