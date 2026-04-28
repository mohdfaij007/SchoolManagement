package com.schoolmanagement.schoolbackend.service.impl;

import com.schoolmanagement.schoolbackend.dto.*;
import com.schoolmanagement.schoolbackend.model.AttendanceStatus;
import com.schoolmanagement.schoolbackend.model.FeeTransaction;
import com.schoolmanagement.schoolbackend.repository.*;
import com.schoolmanagement.schoolbackend.service.FeeCollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl {

    private final StudentEnrollmentRepository enrollmentRepository;
    private final AttendanceRepository attendanceRepository;
    private final FeeTransactionRepository feeTransactionRepository;
    private final FeeCollectionService feeCollectionService;

    public DashboardMetricsDTO getDashboardMetrics() {
        DashboardMetricsDTO metrics = new DashboardMetricsDTO();
        LocalDate today = LocalDate.now();

        // 1. Total Active Students
        metrics.setTotalActiveStudents(enrollmentRepository.countByIsCurrentActiveTrue());

        // 2. Today's Attendance Rate
        long totalMarkedToday = attendanceRepository.countByDate(today);
        long presentToday = attendanceRepository.countByDateAndStatusIn(today, 
                Arrays.asList(AttendanceStatus.PRESENT, AttendanceStatus.LATE, AttendanceStatus.HALFDAY));
        
        double attendanceRate = (totalMarkedToday == 0) ? 0.0 : ((double) presentToday / totalMarkedToday) * 100;
        metrics.setTodayAttendanceRate(Math.round(attendanceRate * 100.0) / 100.0); // Round to 2 decimals

        // 3. Today's Revenue (Reusing our Daily Report logic)
        DailyCollectionReportDTO dailyReport = feeCollectionService.getDailyCollection(today);
        metrics.setTodayRevenue(dailyReport.getTotalCollection());

        // 4. Total Outstanding Dues & Top Defaulters
        // Passing null, null gets the defaulters for the ENTIRE school (lightning fast due to our N+1 fix!)
        List<DefaulterDTO> allDefaulters = feeCollectionService.getDefaultersByClass(null, null);
        
        double totalDues = allDefaulters.stream().mapToDouble(DefaulterDTO::getTotalDueAmount).sum();
        metrics.setTotalOutstandingDues(totalDues);

        // Get only the top 5 highest defaulters
        List<DefaulterDTO> top5Defaulters = allDefaulters.stream().limit(5).collect(Collectors.toList());
        metrics.setTopDefaulters(top5Defaulters);

        // 5. Recent Transactions
        List<FeeTransaction> recentTx = feeTransactionRepository.findTop5ByOrderByTransactionDateDesc();
        List<FeeTransactionDTO> recentTxDTOs = recentTx.stream().map(t -> {
            FeeTransactionDTO dto = new FeeTransactionDTO();
            dto.setTransactionId(t.getId());
            dto.setTotalAmount(t.getTotalAmount());
            dto.setPaymentMode(t.getPaymentMode());
            dto.setTransactionDate(t.getTransactionDate());
            dto.setRemarks(t.getRemarks());
            return dto;
        }).collect(Collectors.toList());
        
        metrics.setRecentTransactions(recentTxDTOs);

        return metrics;
    }
}