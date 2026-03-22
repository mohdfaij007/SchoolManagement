package com.schoolmanagement.schoolbackend.payload.request;

import lombok.Data;
import java.util.List;

@Data
public class SessionClassSetupDTO {
    private Long sessionId;
    private Long standardId;
    private List<Long> sectionIds; // Ek sath multiple sections map karne ke liye (e.g., Class 1 -> Sec A, Sec B)
    private Integer maxCapacity;
}