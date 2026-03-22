package com.schoolmanagement.schoolbackend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class GradeMasterDTO {
    
    private Long id;

    @NotBlank(message = "Grade Name is required (e.g., A1, B2)")
    private String gradeName;

    @NotNull(message = "Min Percentage is required")
    @Min(value = 0, message = "Min Percentage cannot be less than 0")
    @Max(value = 100, message = "Min Percentage cannot be more than 100")
    private Double minPercentage;

    @NotNull(message = "Max Percentage is required")
    @Min(value = 0, message = "Max Percentage cannot be less than 0")
    @Max(value = 100, message = "Max Percentage cannot be more than 100")
    private Double maxPercentage;

    @NotNull(message = "Grade Point is required")
    private Double gradePoint; // For CGPA (e.g. 10.0)

    private String remarks; // Optional (e.g. "Outstanding")
}