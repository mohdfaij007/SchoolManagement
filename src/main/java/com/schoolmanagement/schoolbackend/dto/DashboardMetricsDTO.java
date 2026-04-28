package com.schoolmanagement.schoolbackend.dto;

import lombok.Data;
import java.util.List;

@Data
public class DashboardMetricsDTO {
    // Top Row KPIs
    private long totalActiveStudents;
    private double todayAttendanceRate;
    private double todayRevenue;
    private double totalOutstandingDues;

    // Bottom Row Action Items
    private List<DefaulterDTO> topDefaulters;
    private List<FeeTransactionDTO> recentTransactions;
}