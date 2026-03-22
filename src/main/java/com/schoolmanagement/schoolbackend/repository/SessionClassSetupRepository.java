package com.schoolmanagement.schoolbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.schoolmanagement.schoolbackend.model.SessionClassSetup;
import java.util.List;

public interface SessionClassSetupRepository extends JpaRepository<SessionClassSetup, Long> {
    
    // Ek session mein kitni classes aur sections active hain, uski list nikalne ke liye
    List<SessionClassSetup> findByAcademicSessionId(Long sessionId);
    
    // Ek session aur class mein kitne sections active hain
    List<SessionClassSetup> findByAcademicSessionIdAndStandardId(Long sessionId, Long standardId);
}