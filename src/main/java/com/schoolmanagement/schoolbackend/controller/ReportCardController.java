package com.schoolmanagement.schoolbackend.controller;

import com.schoolmanagement.schoolbackend.dto.reportcard.ReportCardDTO;
import com.schoolmanagement.schoolbackend.service.impl.ReportCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.schoolmanagement.schoolbackend.service.impl.PdfGenerationService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;



@RestController
@RequestMapping("/api/report-card")
public class ReportCardController {

    @Autowired
    private ReportCardService reportCardService;
    
    @Autowired
    private PdfGenerationService pdfGenerationService;

    // Example URL: GET /api/report-card/generate?studentId=1&examIds=1,2&sessionId=1
    @GetMapping("/generate")
    public ResponseEntity<ReportCardDTO> generateReportCard(
            @RequestParam Long studentId,
            @RequestParam List<Long> examIds,
            @RequestParam Long sessionId) {
        
        ReportCardDTO reportCard = reportCardService.generateReportCard(studentId, examIds, sessionId);
        return ResponseEntity.ok(reportCard);
    }
    
    
    @GetMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadReportCardPdf(
            @RequestParam Long studentId,
            @RequestParam List<Long> examIds,
            @RequestParam Long sessionId) {
        
        // 1. Get the data DTO
        ReportCardDTO reportCard = reportCardService.generateReportCard(studentId, examIds, sessionId);
        
        // 2. Generate PDF byte array
        byte[] pdfBytes = pdfGenerationService.generateReportCardPdf(reportCard);

        // 3. Set headers to tell the browser it's a file download
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "ReportCard_" + reportCard.getAdmissionNumber() + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}