package com.schoolmanagement.schoolbackend.payload.request;

import java.time.LocalDate;
import java.util.List;

import com.schoolmanagement.schoolbackend.dto.AttendanceRecordDTO;

import lombok.Data;
@Data
public class BulkAttendanceRequest {
	
	private LocalDate date;
    private Long standardId;
    private Long sectionId;
    private Long academicSessionId;
    private List<AttendanceRecordDTO> students;

}
