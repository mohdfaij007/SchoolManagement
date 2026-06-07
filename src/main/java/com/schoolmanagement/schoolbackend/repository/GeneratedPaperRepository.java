package com.schoolmanagement.schoolbackend.repository;

import com.schoolmanagement.schoolbackend.model.GeneratedPaper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GeneratedPaperRepository extends JpaRepository<GeneratedPaper, Long> {
    // Fetch past papers for a specific class and subject
    List<GeneratedPaper> findByStandardIdAndSubjectIdAndAcademicSessionId(Long standardId, Long subjectId, Long sessionId);
}