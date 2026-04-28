package com.schoolmanagement.schoolbackend.controller;

import com.schoolmanagement.schoolbackend.dto.DashboardMetricsDTO;
import com.schoolmanagement.schoolbackend.service.impl.DashboardServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardServiceImpl dashboardService;

    @GetMapping("/metrics")
    public ResponseEntity<DashboardMetricsDTO> getMetrics() {
        return ResponseEntity.ok(dashboardService.getDashboardMetrics());
    }
}