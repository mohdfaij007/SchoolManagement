package com.schoolmanagement.schoolbackend.service;


import java.time.LocalDate;

import java.util.List;

import org.springframework.stereotype.Service;

import com.schoolmanagement.schoolbackend.dto.AttendanceRecordDTO;
import com.schoolmanagement.schoolbackend.dto.MonthlyRegisterResponseDTO;
import com.schoolmanagement.schoolbackend.model.Attendance;
import com.schoolmanagement.schoolbackend.payload.request.BulkAttendanceRequest;
import com.schoolmanagement.schoolbackend.payload.response.StudentAttendanceSummaryDTO;
@Service
public interface AttendanceService {


	String markBulkAttendance(BulkAttendanceRequest request);
	List<Attendance> getAttendanceByDate(LocalDate date);
	List<AttendanceRecordDTO> getAttendanceForClass(Long stdId, Long secId, LocalDate date);
	List<StudentAttendanceSummaryDTO> getAttendanceDashboard(Long stdId, Long secId, Long sessId, LocalDate selectedDate);

	MonthlyRegisterResponseDTO getMonthlyRegister(Long classId, Long sectionId, Long sessionId, int year, int month);




}
