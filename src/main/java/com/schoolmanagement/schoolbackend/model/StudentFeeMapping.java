package com.schoolmanagement.schoolbackend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "student_fee_mappings", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "fee_structure_id"}))
public class StudentFeeMapping extends BaseTenantEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to the Student
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    // Link to the specific Fee (e.g., Class 10 Tuition Fee)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fee_structure_id", nullable = false)
    private FeeStructure feeStructure;

    // Is this mapping active? (e.g. set false if they stop using the Bus)
    @Column(name = "is_active")
    private boolean isActive = true;
    
    // When was this assigned?
    @Column(name = "assigned_date")
    private LocalDate assignedDate = LocalDate.now();
    
 // 👇 NEW FIELDS: To handle "Bus from Sept to Oct" scenario
    @Column(name = "start_date")
    private LocalDate startDate; 

    @Column(name = "end_date")
    private LocalDate endDate; // Null means "Till end of session"
    
    
    
    
}
