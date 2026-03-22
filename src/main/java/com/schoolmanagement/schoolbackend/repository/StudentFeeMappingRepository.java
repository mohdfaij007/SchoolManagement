package com.schoolmanagement.schoolbackend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.schoolmanagement.schoolbackend.model.StudentFeeMapping;
@Repository
public interface StudentFeeMappingRepository extends JpaRepository<StudentFeeMapping, Long> {
	
	// 1. Get all fees assigned to a specific student
    List<StudentFeeMapping> findByStudentId(Long studentId);
    
    // 2. Check if a specific mapping exists (To avoid duplicates)
    Optional<StudentFeeMapping> findByStudentIdAndFeeStructureId(Long studentId, Long feeStructureId);

    // 3. Find all students who have a specific fee (e.g., "Show me all Bus users")
    List<StudentFeeMapping> findByFeeStructureId(Long feeStructureId);
    
    // 4. Delete specific mapping
    void deleteByStudentIdAndFeeStructureId(Long studentId, Long feeStructureId);

}
