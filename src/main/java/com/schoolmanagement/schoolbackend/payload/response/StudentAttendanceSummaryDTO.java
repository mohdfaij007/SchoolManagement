package com.schoolmanagement.schoolbackend.payload.response;

import lombok.Data;
import java.util.List;

@Data
public class StudentAttendanceSummaryDTO {
    private Long studentId;
    private String name;
    private String admissionNumber;
    private String avatarUrl; // If you have photos
    
    // The Status for TODAY (to set the initial Toggle state)
    private String todayStatus; // "PRESENT", "ABSENT", "NOT_MARKED"

    // The History Bubbles (e.g., ["P", "A", "L", "P", "P"])
    private List<String> history; 
}