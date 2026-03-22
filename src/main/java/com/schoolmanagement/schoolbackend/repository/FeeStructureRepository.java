package com.schoolmanagement.schoolbackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.schoolmanagement.schoolbackend.model.FeeStructure;
@Repository
public interface FeeStructureRepository extends JpaRepository<FeeStructure, Long> {
	
	// Find all fees for a specific class in a specific session
    // (Used when a student opens their fee page)
//    List<FeeStructure> findByClassIdAndAcademicSessionId(Long standardId, Long academicSessionId);
    
	
	List<FeeStructure> findByStandardIdAndAcademicSessionId(Long standardId, Long academicSessionId);
 // inside FeeStructureRepository interface
    List<FeeStructure> findByAcademicSessionId(Long academicSessionId);

}
