package com.schoolmanagement.schoolbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.schoolmanagement.schoolbackend.model.ExamSubjectMapping;
import java.util.List;

@Repository
public interface ExamSubjectMappingRepository extends JpaRepository<ExamSubjectMapping, Long> {
    // Kisi class ke liye kisi exam ka time table / subjects lana
    List<ExamSubjectMapping> findByExamIdAndStandardId(Long examId, Long standardId);
    
    // Duplicate entry rokne ke liye
    boolean existsByExamIdAndStandardIdAndSubjectId(Long examId, Long standardId, Long subjectId);
}