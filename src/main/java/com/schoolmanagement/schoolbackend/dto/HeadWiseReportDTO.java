package com.schoolmanagement.schoolbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HeadWiseReportDTO {
    private String feeHeadName;
    private Double totalAmount;
}