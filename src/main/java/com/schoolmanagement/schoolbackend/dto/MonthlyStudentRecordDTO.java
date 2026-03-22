package com.schoolmanagement.schoolbackend.dto;

import lombok.Data;
import java.util.Map;

@Data
public class MonthlyStudentRecordDTO {
    private Long studentId;
    private String admissionNo;
    private String studentName;
    // Map of Day (1 to 31) -> Status ("P", "A", "L", "H")
    private Map<Integer, String> attendanceMap; 
    private int presentCount;
    private int absentCount;
}