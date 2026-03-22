package com.schoolmanagement.schoolbackend.service.impl;

import com.schoolmanagement.schoolbackend.dto.reportcard.ReportCardDTO;
import com.schoolmanagement.schoolbackend.model.SchoolProfile;
import com.schoolmanagement.schoolbackend.repository.SchoolProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;

@Service
public class PdfGenerationService {

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private SchoolProfileRepository schoolProfileRepository;

    public byte[] generateReportCardPdf(ReportCardDTO reportCardData) {
        try {
            // 1. Fetch School Profile (Agar nahi hai toh default dummy profile use karega)
            SchoolProfile school = schoolProfileRepository.findFirstByIsActiveTrue()
                    .orElseGet(() -> {
                        SchoolProfile dummy = new SchoolProfile();
                        dummy.setSchoolName("YOUR SCHOOL NAME");
                        dummy.setSchoolAddress("City, State - Pincode");
                        dummy.setAffiliationNumber("XXXXX");
                        dummy.setContactPhone("XXX-XXX-XXXX");
                        return dummy;
                    });

            // 2. Load data into Thymeleaf Context
            Context context = new Context();
            context.setVariable("school", school);
            context.setVariable("report", reportCardData);

            // 3. Process HTML template with dynamic data
            String htmlContent = templateEngine.process("report_card", context);

            // 4. Convert HTML to PDF using Flying Saucer
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);

            return outputStream.toByteArray(); // Returns the actual PDF file bytes
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error generating PDF: " + e.getMessage());
        }
    }
}