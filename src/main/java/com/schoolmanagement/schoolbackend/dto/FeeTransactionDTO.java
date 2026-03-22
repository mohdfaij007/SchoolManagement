package com.schoolmanagement.schoolbackend.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.schoolmanagement.schoolbackend.enums.PaymentMode;

import lombok.Data;
@Data
public class FeeTransactionDTO {

	private Long transactionId;
    private Double totalAmount;
    private PaymentMode paymentMode;
    private LocalDateTime transactionDate;
    private String remarks;
    // We can add "List<FeeTransactionDetailDTO>" later if we want to show breakdown on the receipt

 // 👇 ADD THIS NEW LIST
    private List<TransactionDetailItem> breakdown;

    // 👇 Helper Inner Class for the rows
    @Data
    public static class TransactionDetailItem {
        private String feeHeadName; // e.g., "Tuition Fee"
        private Double amount;      // e.g., 2000.00
    }
}
