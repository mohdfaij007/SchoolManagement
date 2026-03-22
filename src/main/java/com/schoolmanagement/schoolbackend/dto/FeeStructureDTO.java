package com.schoolmanagement.schoolbackend.dto;

import com.schoolmanagement.schoolbackend.enums.FeeFrequency;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FeeStructureDTO {

    private Long id;

    @NotNull(message = "Class ID is required")
    private Long classId;
    
    private String className;

    @NotNull(message = "Fee Head ID is required")
    private Long feeHeadId;

    @NotNull(message = "Session ID is required")
    private Long academicSessionId; 

    @NotNull(message = "Amount is required")
    @Min(value = 0, message = "Amount cannot be negative")
    private Double amount;
    
    private String feeHeadName;   // e.g., "Tuition Fee"
    private FeeFrequency frequency;     // e.g., "MONTHLY"
    
    private Boolean isMandatory;
    
    
    
    
	public Boolean getIsMandatory() {
		return isMandatory;
	}

	public void setIsMandatory(Boolean isMandatory) {
		this.isMandatory = isMandatory;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getFeeHeadName() {
		return feeHeadName;
	}

	public void setFeeHeadName(String feeHeadName) {
		this.feeHeadName = feeHeadName;
	}

	public FeeFrequency getFrequency() {
		return frequency;
	}

	public void setFrequency(FeeFrequency frequency) {
		this.frequency = frequency;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getClassId() {
		return classId;
	}

	public void setClassId(Long classId) {
		this.classId = classId;
	}

	public Long getFeeHeadId() {
		return feeHeadId;
	}

	public void setFeeHeadId(Long feeHeadId) {
		this.feeHeadId = feeHeadId;
	}

	public Long getAcademicSessionId() {
		return academicSessionId;
	}

	public void setAcademicSessionId(Long academicSessionId) {
		this.academicSessionId = academicSessionId;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}
    
    
    
}