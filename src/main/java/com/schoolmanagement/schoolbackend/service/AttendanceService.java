package com.schoolmanagement.schoolbackend.service;


import com.schoolmanagement.schoolbackend.model.Attendance;
import com.schoolmanagement.schoolbackend.model.Student;
import com.schoolmanagement.schoolbackend.payload.request.AttendanceRequest;
import com.schoolmanagement.schoolbackend.repository.AttendanceRepository;
import com.schoolmanagement.schoolbackend.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
@Service
public class AttendanceService {


    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private StudentRepository studentRepository;

    public Attendance markAttendance(AttendanceRequest request) {
        // 1. Fetch Student from DB to ensure they exist
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + request.getStudentId()));

        // 2. Check if attendance is already marked for this day (Prevent duplicates)
        if (attendanceRepository.existsByStudentIdAndDate(request.getStudentId(), request.getDate())) {
            throw new RuntimeException("Attendance already marked for student " + request.getStudentId() + " on " + request.getDate());
        }

        // 3. Create Attendance Object
        Attendance attendance = new Attendance();
        attendance.setStudent(student); // Link the relationship
        attendance.setDate(request.getDate());
        attendance.setStatus(request.getStatus());
        attendance.setRemarks(request.getRemarks());

        // 4. Save to Database
        return attendanceRepository.save(attendance);
    }

    public List<Attendance> getAttendanceByDate(LocalDate date) {
        return attendanceRepository.findByDate(date);
    }
}
