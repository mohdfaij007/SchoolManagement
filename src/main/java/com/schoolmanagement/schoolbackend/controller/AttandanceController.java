package com.schoolmanagement.schoolbackend.controller;
import com.schoolmanagement.schoolbackend.dto.AttendanceRecordDTO;
import com.schoolmanagement.schoolbackend.dto.MonthlyRegisterResponseDTO;
import com.schoolmanagement.schoolbackend.model.Attendance;
import com.schoolmanagement.schoolbackend.model.Student;
import com.schoolmanagement.schoolbackend.payload.request.AttendanceRequest;
import com.schoolmanagement.schoolbackend.payload.request.BulkAttendanceRequest;
import com.schoolmanagement.schoolbackend.payload.response.StudentAttendanceSummaryDTO;
import com.schoolmanagement.schoolbackend.repository.StudentRepository;
import com.schoolmanagement.schoolbackend.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttandanceController {
	@Autowired
    private AttendanceService attendanceService;
	


    // POST: Mark Attendance
	// POST: Mark Bulk Attendance (The "Enterprise" Way)
    @PostMapping("/bulk")
    public ResponseEntity<?> markBulkAttendance(@RequestBody BulkAttendanceRequest request) {
        try {
            // The service now handles the transaction and looping
            String message = attendanceService.markBulkAttendance(request);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            // In a real app, use a Global Exception Handler for better error messages
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET: View Attendance by Date (e.g., /api/attendance?date=2023-10-27)
    @GetMapping
    public ResponseEntity<List<Attendance>> getAttendanceByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Attendance> attendanceList = attendanceService.getAttendanceByDate(date);
        return new ResponseEntity<>(attendanceList, HttpStatus.OK);
    }
	
    
    // Get Attendance by class
    @GetMapping("/class-record")
    public ResponseEntity<List<AttendanceRecordDTO>> getClassAttendance(
            @RequestParam Long standardId,
            @RequestParam Long sectionId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        List<AttendanceRecordDTO> records = attendanceService.getAttendanceForClass(standardId, sectionId, date);
        
        if (records.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content (Signal to App: "Start Fresh")
        }
        return ResponseEntity.ok(records);
    }
	
    
    @GetMapping("/dashboard")
    public ResponseEntity<?> getAttendanceDashboard(
            @RequestParam Long standardId,
            @RequestParam Long sectionId,
            @RequestParam Long sessionId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
    	try {
            List<StudentAttendanceSummaryDTO> dashboardData = attendanceService.getAttendanceDashboard(standardId, sectionId, sessionId, date);
            return ResponseEntity.ok(dashboardData);
        } catch (RuntimeException e) {
            // Spring Boot ka default 500 error rokne ke liye 400 Bad Request return kar rahe hain
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    
    @GetMapping("/monthly-register")
    public ResponseEntity<?> getMonthlyRegister(
            @RequestParam Long standardId,
            @RequestParam Long sectionId,
            @RequestParam Long sessionId,
            @RequestParam int year,
            @RequestParam int month
    ) {
        try {
            // Note: Make sure to add this method signature to AttendanceService Interface if you use one
            MonthlyRegisterResponseDTO response = attendanceService.getMonthlyRegister(standardId, sectionId, sessionId, year, month);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
