package com.schoolmanagement.schoolbackend.service;

import java.time.LocalDate;
import java.util.List;


import com.schoolmanagement.schoolbackend.dto.DailyCollectionReportDTO;
import com.schoolmanagement.schoolbackend.dto.DefaulterDTO;
import com.schoolmanagement.schoolbackend.dto.FeeDueReportDTO;
import com.schoolmanagement.schoolbackend.dto.FeeTransactionDTO;
import com.schoolmanagement.schoolbackend.dto.HeadWiseReportDTO;
import com.schoolmanagement.schoolbackend.dto.PaymentRequestDTO;
import com.schoolmanagement.schoolbackend.model.FeeTransaction;
public interface FeeCollectionService {
	
	public FeeDueReportDTO getDueReport(Long studentId);
	
//	public FeeTransaction collectFees(PaymentRequestDTO request);
	
	List<FeeTransactionDTO> getTransactionHistory(Long studentId);
	
	// Add this method
	List<DefaulterDTO> getDefaultersByClass(Long classId, Long sectionId);
	
	DailyCollectionReportDTO getDailyCollection(LocalDate date);
	
	List<HeadWiseReportDTO> getHeadWiseReport(Long sessionId);
	
	// Update this line:
    FeeTransaction collectFees(PaymentRequestDTO request, Long collectedByUserId);

}
