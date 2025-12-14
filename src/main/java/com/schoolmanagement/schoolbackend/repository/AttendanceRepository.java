package com.schoolmanagement.schoolbackend.repository;

import com.schoolmanagement.schoolbackend.model.Attendance;
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
}