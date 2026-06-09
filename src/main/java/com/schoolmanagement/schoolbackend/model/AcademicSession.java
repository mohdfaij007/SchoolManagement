package com.schoolmanagement.schoolbackend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "academic_sessions", uniqueConstraints = {
	    // Ab session Name sirf ek particular school ke andar unique rahega
	    @UniqueConstraint(columnNames = {"sessionName", "school_profile_id"})
	})
public class AcademicSession extends BaseTenantEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sessionName; // Example: "2025-2026"

    private LocalDate startDate; // Example: 2025-04-01
    private LocalDate endDate;   // Example: 2026-03-31

    @Column(name = "is_active")
    private boolean isActive = false; // जो साल चल रहा है उसे true करेंगे

	public String getSessionName() {
		return sessionName;
	}

	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public Long getId() {
		return id;
	}
    
    
}