package com.felix.healthcare.api_core.service;

import com.felix.healthcare.api_core.dto.LabResultDto;
import com.felix.healthcare.api_core.entity.LabResult;
import com.felix.healthcare.api_core.entity.Patient;
import com.felix.healthcare.api_core.repository.LabResultRepository;
import com.felix.healthcare.api_core.repository.PatientRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LabResultServiceTest {

    @InjectMocks
    private LabResultService labResultService;

    @Mock
    private LabResultRepository labResultRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @Test
    void saveFromExcel() throws IOException, ExecutionException, InterruptedException {
        // Mock input data
        String jobId = "testJobId";
        byte[] fileBytes = createMockExcelFile();

        // Mock Cache behavior
        Mockito.when(cacheManager.getCache("jobStatusCache")).thenReturn(cache);

        // Mock PatientRepository behavior
        Patient mockPatient = new Patient();
        mockPatient.setId(1L);
        Mockito.when(patientRepository.findById(1L)).thenReturn(Optional.of(mockPatient));

        // Call the method and get the CompletableFuture
        CompletableFuture<LabResultDto.BulkUploadSummaryResponse> future =
                labResultService.saveFromExcel(fileBytes, jobId);

        // Wait for the async task to complete and get the result
        LabResultDto.BulkUploadSummaryResponse response = future.get();

        // Assert the result
        Assertions.assertNotNull(response);
        Assertions.assertEquals(4, response.getErrors());
        Assertions.assertEquals(10, response.getRecords());

        // Verify interactions
        Mockito.verify(cache).put(jobId, "IN_PROGRESS");
        Mockito.verify(cache).put(jobId, "COMPLETED");
        Mockito.verify(labResultRepository, Mockito.atLeastOnce()).save(Mockito.any(LabResult.class));
    }

    private byte[] createMockExcelFile() throws IOException {
        // Create a mock Excel file with Apache POI
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Sheet1");

            // Create header row
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("patientId");
            header.createCell(1).setCellValue("testType");
            header.createCell(2).setCellValue("sampleData");

            // Create data rows
            Object[][] data = {
                    {1, "Blood-1", "blood1"},
                    {2, "Blood-2", "blood2"},
                    {3, "Blood-3", "blood3"},
                    {1, "Blood-4", "blood4"},
                    {2, "Blood-5", "blood5"},
                    {3, "Blood-6", "blood6"},
                    {1, "Blood-7", "blood7"},
                    {1, "Blood-8", "blood8"},
                    {1, "Blood-9", "blood9"},
                    {1, "Blood-10", "blood10"}
            };

            int rowNum = 1;
            for (Object[] rowData : data) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue((int) rowData[0]);
                row.createCell(1).setCellValue((String) rowData[1]);
                row.createCell(2).setCellValue((String) rowData[2]);
            }

            // Write the workbook to a byte array
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                workbook.write(outputStream);
                return outputStream.toByteArray();
            }
        }
    }
}