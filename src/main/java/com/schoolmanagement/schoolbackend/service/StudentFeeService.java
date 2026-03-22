package com.schoolmanagement.schoolbackend.service;

import java.time.LocalDate;
import java.util.List;

import com.schoolmanagement.schoolbackend.dto.StudentFeeDTO;

public interface StudentFeeService {
	
	// 1. Assign all mandatory fees to a student (Used during Admission)
    void assignMandatoryFees(Long studentId, Long classId, Long sessionId);

    // 2. Get all available fees for a student (Assigned + Unassigned Options)
    // This allows the UI to show checkboxes: [x] Tuition (Mandatory)  [ ] Bus (Optional)
    List<StudentFeeDTO> getFeeOptionsForStudent(Long studentId, Long classId, Long sessionId);

    // 3. Toggle a specific fee (Assign/Unassign)
//    void toggleFeeForStudent(Long studentId, Long feeStructureId, boolean shouldAssign);
    void updateFeeStatus(Long studentId, Long feeStructureId, Boolean isActive, LocalDate startDate, LocalDate endDate);

	

}
