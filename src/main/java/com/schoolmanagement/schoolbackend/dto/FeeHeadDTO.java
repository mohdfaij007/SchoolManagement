package com.schoolmanagement.schoolbackend.dto;

import com.schoolmanagement.schoolbackend.enums.FeeFrequency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FeeHeadDTO {
    
    private Long id; // Null when creating new, present when updating

    @NotBlank(message = "Fee Name is required")
    private String headName;

    private String description;

    @NotNull(message = "Frequency is required")
    private FeeFrequency frequency;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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
    
    
    
}