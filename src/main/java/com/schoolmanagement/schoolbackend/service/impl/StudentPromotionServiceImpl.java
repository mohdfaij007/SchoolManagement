package com.schoolmanagement.schoolbackend.service.impl;

import com.schoolmanagement.schoolbackend.dto.BulkPromotionRequestDTO;
import com.schoolmanagement.schoolbackend.dto.PromotionDetailDTO;
import com.schoolmanagement.schoolbackend.enums.PromotionStatus;
import com.schoolmanagement.schoolbackend.model.*;
import com.schoolmanagement.schoolbackend.repository.*;
import com.schoolmanagement.schoolbackend.service.StudentFeeService;
import com.schoolmanagement.schoolbackend.service.StudentPromotionService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class StudentPromotionServiceImpl implements StudentPromotionService {

    private final StudentEnrollmentRepository studentEnrollmentRepository;
    private final AcademicSessionRepository academicSessionRepository;
    private final StandardRepository standardRepository;
    private final SectionRepository sectionRepository;
    private final StudentRepository studentRepository;
    
    // We inject the Fee Service so we can auto-bill them for the new year!
    private final StudentFeeService studentFeeService;

    @Override
    @Transactional
    public String processBulkPromotion(BulkPromotionRequestDTO requestDTO) {
        
        // 1. Fetch the target academic session (e.g., 2026-2027)
        AcademicSession nextSession = academicSessionRepository.findById(requestDTO.getNextSessionId())
                .orElseThrow(() -> new RuntimeException("Target Academic Session not found"));

        int successCount = 0;

        // 2. Loop through each student in the batch
        for (PromotionDetailDTO detail : requestDTO.getStudents()) {
            Student student = studentRepository.findById(detail.getStudentId())
                    .orElseThrow(() -> new RuntimeException("Student not found with ID: " + detail.getStudentId()));

            // 3. Find their current active enrollment and deactivate it (so they leave the old class)
            StudentEnrollment currentEnrollment = studentEnrollmentRepository
                    .findByStudentIdAndIsCurrentActiveTrue(student.getId());

            if (currentEnrollment != null) {
                currentEnrollment.setCurrentActive(false);
                studentEnrollmentRepository.save(currentEnrollment);
            }

            // 4. Process based on the teacher's decision
            if (detail.getPromotionStatus() == PromotionStatus.PROMOTED || 
                detail.getPromotionStatus() == PromotionStatus.RETAINED) {

                Standard nextClass = standardRepository.findById(detail.getNextClassId())
                        .orElseThrow(() -> new RuntimeException("Target Class not found"));
                
                Section nextSection = null;
                if (detail.getNextSectionId() != null) {
                    nextSection = sectionRepository.findById(detail.getNextSectionId())
                            .orElseThrow(() -> new RuntimeException("Target Section not found"));
                }

                // Create a brand new enrollment for the new academic year
                StudentEnrollment newEnrollment = new StudentEnrollment();
                newEnrollment.setStudent(student);
                newEnrollment.setAcademicSession(nextSession);
                newEnrollment.setStandard(nextClass);
                newEnrollment.setSection(nextSection);
                newEnrollment.setEnrollmentDate(LocalDate.now());
                newEnrollment.setCurrentActive(true); // Make this the active one!

                studentEnrollmentRepository.save(newEnrollment);

                // 5. AUTO-ASSIGN FEES FOR THE NEW YEAR
                studentFeeService.assignMandatoryFees(student.getId(), nextClass.getId(), nextSession.getId());
                
                successCount++;
                
            } else if (detail.getPromotionStatus() == PromotionStatus.LEFT_SCHOOL) {
                // They left. We already deactivated their old enrollment above. 
                // We do not create a new one. 
                successCount++;
            }
        }

        return "Successfully processed promotion/retention for " + successCount + " students.";
    }
}