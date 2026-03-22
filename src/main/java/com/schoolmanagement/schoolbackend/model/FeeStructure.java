package com.schoolmanagement.schoolbackend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "fee_structures", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"class_id", "fee_head_id", "academic_session_id"}) 
    // ^ Prevents duplicate fees for the same head in the same class & session
})
public class FeeStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

 // --- NEW OPTIMIZED CODE ---
    @ManyToOne(fetch = FetchType.EAGER) // Eager loads data instantly (Good for simple relations)
    @JoinColumn(name = "class_id", nullable = false) // Maps to the existing 'class_id' column in DB
    private Standard standard;

    // The Type of Fee (e.g., Tuition)
    @ManyToOne
    @JoinColumn(name = "fee_head_id", nullable = false)
    private FeeHead feeHead;

    // The Session (e.g., 2025-2026) -> LINKED TO YOUR ENTITY
    @ManyToOne
    @JoinColumn(name = "academic_session_id", nullable = false)
    private AcademicSession academicSession;

    @Column(nullable = false)
    private Double amount; // The cost (e.g., 2500.00)
    
    @Column(name = "is_mandatory", nullable = false, columnDefinition = "boolean default true")
    private Boolean isMandatory = true; // Default to true (Safe choice)
    
    public Boolean getIsMandatory() {
		return isMandatory;
	}



	public void setIsMandatory(Boolean isMandatory) {
		this.isMandatory = isMandatory;
	}



	// Helper method to update amount safely
    public void updateAmount(Double newAmount) {
        this.amount = newAmount;
    }



	public Standard getStandard() {
		return standard;
	}



	public void setStandard(Standard standard) {
		this.standard = standard;
	}



	public FeeHead getFeeHead() {
		return feeHead;
	}

	public void setFeeHead(FeeHead feeHead) {
		this.feeHead = feeHead;
	}

	public AcademicSession getAcademicSession() {
		return academicSession;
	}

	public void setAcademicSession(AcademicSession academicSession) {
		this.academicSession = academicSession;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Long getId() {
		return id;
	}
    
    
    
    
    
}