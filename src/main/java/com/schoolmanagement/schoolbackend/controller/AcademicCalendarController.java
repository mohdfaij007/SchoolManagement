package com.schoolmanagement.schoolbackend.controller;

import com.schoolmanagement.schoolbackend.dto.AcademicCalendarDTO;
import com.schoolmanagement.schoolbackend.payload.request.BulkVacationRequest;
import com.schoolmanagement.schoolbackend.service.impl.AcademicCalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calendar")
public class AcademicCalendarController {

    @Autowired
    private AcademicCalendarService calendarService;

    // 1. Get Session Calendar
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<AcademicCalendarDTO>> getCalendar(@PathVariable Long sessionId) {
        return ResponseEntity.ok(calendarService.getCalendarBySession(sessionId));
    }

    // 2. Add Single Holiday
    @PostMapping
    public ResponseEntity<?> addHoliday(@RequestBody AcademicCalendarDTO dto) {
        try {
            return new ResponseEntity<>(calendarService.addSingleHoliday(dto), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 3. Mark All Sundays
    @PostMapping("/bulk-sundays/{sessionId}")
    public ResponseEntity<?> markAllSundays(@PathVariable Long sessionId) {
        try {
            return ResponseEntity.ok(calendarService.markAllSundays(sessionId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 4. Mark Date Range Vacation
    @PostMapping("/bulk-vacation")
    public ResponseEntity<?> markVacationRange(@RequestBody BulkVacationRequest request) {
        try {
            return ResponseEntity.ok(calendarService.markVacationRange(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 5. Delete Holiday
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHoliday(@PathVariable Long id) {
        calendarService.deleteHoliday(id);
        return ResponseEntity.ok("Holiday deleted successfully");
    }
}