package com.schoolmanagement.schoolbackend.payload.request;

import com.schoolmanagement.schoolbackend.model.AttendanceStatus;
import lombok.Data;
import java.time.LocalDate;

@Data
public class AttendanceRequest {
    private Long studentId;
    private LocalDate date;
    private AttendanceStatus status;
    public Long getStudentId() {
		return studentId;
	}
	public void setStudentId(Long studentId) {
		this.studentId = studentId;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public AttendanceStatus getStatus() {
		return status;
	}
	public void setStatus(AttendanceStatus status) {
		this.status = status;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	private String remarks;
}