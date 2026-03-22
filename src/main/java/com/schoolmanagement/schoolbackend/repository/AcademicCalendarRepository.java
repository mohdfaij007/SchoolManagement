package com.schoolmanagement.schoolbackend.repository;

import com.schoolmanagement.schoolbackend.model.AcademicCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AcademicCalendarRepository extends JpaRepository<AcademicCalendar, Long> {
    
    // Pure session ka calendar nikalne ke liye
    List<AcademicCalendar> findByAcademicSessionIdOrderByDateAsc(Long sessionId);
    
    Optional<AcademicCalendar> findByAcademicSessionIdAndDate(Long sessionId, LocalDate date);
    
    // Ek specific date check karne ke liye (Attendance mark karte waqt kaam aayega)
    boolean existsByAcademicSessionIdAndDate(Long sessionId, LocalDate date);
    
 // Naya method: Mahine ke saare holidays lane ke liye
    List<AcademicCalendar> findByAcademicSessionIdAndDateBetween(Long sessionId, LocalDate startDate, LocalDate endDate);
}