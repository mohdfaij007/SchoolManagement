package com.schoolmanagement.schoolbackend.dto;

import lombok.Data;

@Data
public class FeeHeadDueDTO {
    private Long mappingId;       // We need this ID to link the payment later
    private String feeHeadName;   // "Tuition Fee"
 // 1. Full Year Values
    private Double totalSessionAmount; // (e.g., 12000 for Tuition)
    
    // 2. Strict "Till Date" Values
    private Double amountAccruedTillDate; // (e.g., 10000 till Jan)
    
    // 3. Payment Status
    private Double paidAmount;        // (e.g., 9000)
    
    // 4. The Results
    private Double dueAmountStrict;   // (10000 - 9000 = 1000) -> DEFAULT PAYABLE
    private Double dueAmountFull;     // (12000 - 9000 = 3000) -> MAX PAYABLE
    private int priority;         // To decide order of payment (optional, logic based)
    private String paidUptoMonth; // e.g. "SEPTEMBER"
}