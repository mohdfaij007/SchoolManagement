package com.schoolmanagement.schoolbackend.dto;

import lombok.Data;
import java.util.List;

@Data
public class DailyCollectionReportDTO {
    private String date;
    
    // The Summary (The most important part)
    private Double totalCollection;
    private Double totalCash;
    private Double totalOnline;
    private Double totalCheque;
    
    // The Detailed List (Reusing your existing Transaction DTO)
    private List<FeeTransactionDTO> transactions;
    

}