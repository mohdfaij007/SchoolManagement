package com.schoolmanagement.schoolbackend.repository;

import com.schoolmanagement.schoolbackend.model.StudentMarks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentMarksRepository extends JpaRepository<StudentMarks, Long> {
    
    // Ek specific subject/exam ke saare marks nikalne ke liye
    List<StudentMarks> findByExamSubjectMappingId(Long mappingId);

    // Check karne ke liye ki kya bachhe ka mark pehle se save hai
    Optional<StudentMarks> findByExamSubjectMappingIdAndStudentId(Long mappingId, Long studentId);
}