package com.schoolmanagement.schoolbackend.dto;

import com.schoolmanagement.schoolbackend.model.AttendanceStatus;

import lombok.Data;
@Data
public class AttendanceRecordDTO {
	
	private Long studentId;
    private AttendanceStatus status;
    private String remarks;
    private String studentName;
    private String admissionNo;

}
