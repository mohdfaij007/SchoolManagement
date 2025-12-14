package com.schoolmanagement.schoolbackend.controller;
import com.schoolmanagement.schoolbackend.model.Attendance;
import com.schoolmanagement.schoolbackend.payload.request.AttendanceRequest;
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
    @PostMapping
    public ResponseEntity<?> markAttendance(@RequestBody AttendanceRequest request) {
        try {
            Attendance attendance = attendanceService.markAttendance(request);
            return new ResponseEntity<>(attendance, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // GET: View Attendance by Date (e.g., /api/attendance?date=2023-10-27)
    @GetMapping
    public ResponseEntity<List<Attendance>> getAttendanceByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Attendance> attendanceList = attendanceService.getAttendanceByDate(date);
        return new ResponseEntity<>(attendanceList, HttpStatus.OK);
    }
	
	

}
