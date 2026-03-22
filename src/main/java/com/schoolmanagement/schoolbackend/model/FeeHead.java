package com.schoolmanagement.schoolbackend.model;

import com.schoolmanagement.schoolbackend.enums.FeeFrequency;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;


@Entity
@Data
@Table(name = "fee_heads")
public class FeeHead {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String headName; // e.g., "Tuition Fee", "Sports Fee"

    private String description; // Optional: "Fee for swimming and gym"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeeFrequency frequency; // MONTHLY, ANNUALLY, etc.

	public String getHeadName() {
		return headName;
	}

	public void setHeadName(String headName) {
		this.headName = headName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
    
    
    
    
}
