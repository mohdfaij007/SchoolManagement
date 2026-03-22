package com.schoolmanagement.schoolbackend.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class MonthlyRegisterResponseDTO {
    private int year;
    private int month;
    private int daysInMonth;
    // Map of Day (1 to 31) -> Holiday Code ("W" for Weekend, "HO" for Holiday)
    private Map<Integer, String> holidayMap; 
    private List<MonthlyStudentRecordDTO> students;
}