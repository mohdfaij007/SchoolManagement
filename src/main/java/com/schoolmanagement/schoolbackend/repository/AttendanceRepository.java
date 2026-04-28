package com.schoolmanagement.schoolbackend.repository;

import com.schoolmanagement.schoolbackend.model.Attendance;
import com.schoolmanagement.schoolbackend.model.Section;
import com.schoolmanagement.schoolbackend.model.Standard;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    
    // Find attendance for a specific date (e.g., Today's attendance for the whole school)
    List<Attendance> findByDate(LocalDate date);

    // Find attendance for a specific student
    List<Attendance> findByStudentId(Long studentId);
    
    // Check if attendance already exists for a student on a specific date (to prevent duplicates)
    boolean existsByStudentIdAndDate(Long studentId, LocalDate date);
    
    boolean existsByStandardAndSectionAndDate(Standard standard, Section section, LocalDate date);
    
 // Add this method inside the interface
    List<Attendance> findByStandardIdAndSectionIdAndDate(Long standardId, Long sectionId, LocalDate date);

	List<Attendance> findByStandardIdAndSectionIdAndDateBetween(Long stdId, Long secId, LocalDate fiveDaysAgo,
			LocalDate today);

	List<Attendance> findByStudentIdAndAcademicSessionId(Long studentId, Long sessionId);
	
	long countByDate(java.time.LocalDate date);
	long countByDateAndStatusIn(java.time.LocalDate date, java.util.List<com.schoolmanagement.schoolbackend.model.AttendanceStatus> statuses);
}