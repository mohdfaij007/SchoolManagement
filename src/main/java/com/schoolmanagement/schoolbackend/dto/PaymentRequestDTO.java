package com.schoolmanagement.schoolbackend.dto;

import com.schoolmanagement.schoolbackend.enums.PaymentMode;
import lombok.Data;
import java.util.Map;

@Data
public class PaymentRequestDTO {
    private Long studentId;
    private Double amount;
    private PaymentMode paymentMode; // CASH, ONLINE, etc.
    private String remarks;
    
    // Optional: If they want to specify "Pay 1000 for Bus specifically"
    // Key = MappingId, Value = Amount
    private Map<Long, Double> customAllocation; 
}