package com.schoolmanagement.schoolbackend.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.schoolmanagement.schoolbackend.dto.HeadWiseReportDTO;
import com.schoolmanagement.schoolbackend.model.FeeTransaction;

@Repository
public interface FeeTransactionRepository extends JpaRepository<FeeTransaction, Long> {
	
	// 1. Get History for a Student (Ordered latest first)
    List<FeeTransaction> findByStudentIdOrderByTransactionDateDesc(Long studentId);
    
    // 2. Optimized Dashboard Query: Get Total Collection for a specific Session
    // returns a single number (Sum) instead of fetching thousands of rows
    @Query("SELECT SUM(t.totalAmount) FROM FeeTransaction t WHERE t.academicSessionId = :sessionId")
    Double getTotalCollectionBySession(Long sessionId);
    
    // 3. Get Collection for Today (For Day-End Reports)
    // We use a custom query to handle Date ranges if needed, or simple naming convention
    // List<FeeTransaction> findByTransactionDateBetween(LocalDateTime start, LocalDateTime end);
    
 // Fetch transactions between two timestamps
    List<FeeTransaction> findByTransactionDateBetween(LocalDateTime start, LocalDateTime end);
    
 // 4. Get Head-wise Collection for a specific Session
    @Query("SELECT new com.schoolmanagement.schoolbackend.dto.HeadWiseReportDTO(d.feeHeadName, SUM(d.amountPaid)) " +
           "FROM FeeTransactionDetail d JOIN d.feeTransaction t " +
           "WHERE t.academicSessionId = :sessionId " +
           "GROUP BY d.feeHeadName")
    List<HeadWiseReportDTO> getHeadWiseCollection(Long sessionId);
    
    
    // 5.  this allow Spring Data JPA to fetch data for multiple students at once using an SQL IN (...) clause.
    List<FeeTransaction> findByStudentIdIn(List<Long> studentIds);
    
    
    List<com.schoolmanagement.schoolbackend.model.FeeTransaction> findTop5ByOrderByTransactionDateDesc();

}
