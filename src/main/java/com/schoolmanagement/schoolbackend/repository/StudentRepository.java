package com.schoolmanagement.schoolbackend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.schoolmanagement.schoolbackend.model.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

	
	@Query("SELECT DISTINCT s FROM Student s " +
	           "JOIN s.enrollments e " +
	           "WHERE e.isCurrentActive = true " +
	           "AND (:keyword IS NULL OR :keyword = '' OR " +
	           " LOWER(s.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
	           " LOWER(s.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
	           " LOWER(s.admissionNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
	           "AND (:standardId IS NULL OR e.standard.id = :standardId) " +
	           "AND (:sectionId IS NULL OR e.section.id = :sectionId)")
	    Page<Student> searchStudents(
	            @Param("keyword") String keyword,
	            @Param("standardId") Long standardId,
	            @Param("sectionId") Long sectionId,
	            Pageable pageable
	    );
	
	// Optional: If you want to filter by Class (Standard) as well
	// List<Student> findByStandardId(Long standardId);

	Optional<Student> findById(Long id);
	
	// inside StudentRepository interface
//	List<Student> findByStandardIdAndAcademicSessionId(Long standardId, Long academicSessionId);
	
//	List<Student> findByStandardId(Long standardId);
//	List<Student> findByStandardIdAndSectionId(Long standardId, Long sectionId);

//	List<Student> findByStandardIdAndSectionIdAndAcademicSessionId(Long standardId, Long sectionId, Long sessionId);
	
	

}
