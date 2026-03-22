package com.schoolmanagement.schoolbackend.dto;

import lombok.Data;
import java.util.List;

@Data
public class FeeDueReportDTO {
    private Long studentId;
    private String studentName;
    private String className;
    private Double totalFeeAmount;  // Total defined fees
    private Double totalPaidAmount; // Total collected so far
    private Double netDueAmount;    // The Gap (Defined - Paid)
    
    // The breakdown (e.g., Tuition: Due 2000, Bus: Due 500)
    private List<FeeHeadDueDTO> dues;
}