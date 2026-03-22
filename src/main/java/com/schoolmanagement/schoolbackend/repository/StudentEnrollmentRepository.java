package com.schoolmanagement.schoolbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.schoolmanagement.schoolbackend.model.StudentEnrollment;

import java.util.List;

public interface StudentEnrollmentRepository extends JpaRepository<StudentEnrollment, Long> {
    
    // Ek class, section aur session ke saare bachhe nikalne ke liye
    List<StudentEnrollment> findByStandardIdAndSectionIdAndAcademicSessionId(Long standardId, Long sectionId, Long sessionId);
    
    // Ek bachhe ki current active class nikalne ke liye
    StudentEnrollment findByStudentIdAndIsCurrentActiveTrue(Long studentId);
    
    List<StudentEnrollment> findByStandardIdAndSectionIdAndAcademicSessionIdAndIsCurrentActiveTrue(
    	    Long standardId, Long sectionId, Long sessionId);
    
    
    
 // Ek specific Class aur Section ke active students nikalne ke liye
    List<StudentEnrollment> findByStandardIdAndSectionIdAndIsCurrentActiveTrue(Long standardId, Long sectionId);

    // Ek poori Class (bina section filter) ke active students nikalne ke liye
    List<StudentEnrollment> findByStandardIdAndIsCurrentActiveTrue(Long standardId);
    	    
}