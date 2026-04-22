package com.schoolmanagement.schoolbackend.service.impl;

import com.schoolmanagement.schoolbackend.dto.*;
import com.schoolmanagement.schoolbackend.enums.FeeFrequency;
import com.schoolmanagement.schoolbackend.enums.PaymentMode;
import com.schoolmanagement.schoolbackend.model.*;
import com.schoolmanagement.schoolbackend.repository.*;
import com.schoolmanagement.schoolbackend.service.FeeCollectionService;
import com.schoolmanagement.schoolbackend.service.StudentFeeService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeeCollectionServiceImpl implements FeeCollectionService {

	private final StudentRepository studentRepository;
	private final StudentFeeMappingRepository mappingRepository;
	private final FeeTransactionRepository transactionRepository;
	private final UserRepository userRepository;
	private final StudentEnrollmentRepository studentEnrollmentRepository;

	@Autowired
	private StudentFeeService studentFeeService;

	// =================================================================================
	// HELPER: CORE MATH LOGIC (Extracted to be reused by both Single and Bulk methods)
	// =================================================================================
	private FeeDueReportDTO calculateDues(Student student, StudentEnrollment enrollment, 
										  List<StudentFeeMapping> mappings, List<FeeTransaction> transactions) {
		
		Map<Long, Double> paidMap = new HashMap<>();
		if (transactions != null) {
			for (FeeTransaction t : transactions) {
				for (FeeTransactionDetail d : t.getTransactionDetails()) {
					Long mapId = d.getStudentFeeMapping().getId();
					paidMap.put(mapId, paidMap.getOrDefault(mapId, 0.0) + d.getAmountPaid());
				}
			}
		}

		FeeDueReportDTO report = new FeeDueReportDTO();
		report.setStudentId(student.getId());
		report.setStudentName(student.getFirstName() + " " + student.getLastName());
		report.setClassName(enrollment.getStandard().getGradeName());

		List<FeeHeadDueDTO> dueList = new ArrayList<>();
		double totalStrictDue = 0;
		double totalFullDue = 0;
		double totalPaid = 0;

		LocalDate today = LocalDate.now();
		LocalDate strictCutOff = today.withDayOfMonth(today.lengthOfMonth());

		LocalDate sessionStart = LocalDate.now().withMonth(4).withDayOfMonth(1);
		if (today.getMonthValue() < 4) sessionStart = LocalDate.of(today.getYear() - 1, 4, 1);
		LocalDate sessionEnd = sessionStart.plusYears(1).minusDays(1);

		if (enrollment.getAcademicSession() != null && enrollment.getAcademicSession().getStartDate() != null) {
			sessionStart = enrollment.getAcademicSession().getStartDate();
			sessionEnd = enrollment.getAcademicSession().getEndDate();
		}

		if (mappings != null) {
			for (StudentFeeMapping mapping : mappings) {
				if (!mapping.isActive()) continue;

				FeeFrequency frequency = mapping.getFeeStructure().getFeeHead().getFrequency();
				double rate = mapping.getFeeStructure().getAmount();

				LocalDate start = (mapping.getStartDate() != null) ? mapping.getStartDate() : sessionStart;
				LocalDate end = (mapping.getEndDate() != null) ? mapping.getEndDate() : sessionEnd;

				if (start.isBefore(sessionStart)) start = sessionStart;
				if (end.isAfter(sessionEnd)) end = sessionEnd;

				double sessionTotal = 0;
				double accruedTotal = 0;

				switch (frequency) {
				case MONTHLY:
					long totalMonths = ChronoUnit.MONTHS.between(start.withDayOfMonth(1), end.withDayOfMonth(1)) + 1;
					sessionTotal = rate * totalMonths;

					LocalDate strictEnd = end.isBefore(strictCutOff) ? end : strictCutOff;
					if (!strictEnd.isBefore(start)) {
						long accruedMonths = ChronoUnit.MONTHS.between(start.withDayOfMonth(1), strictEnd.withDayOfMonth(1)) + 1;
						accruedTotal = rate * accruedMonths;
					}
					break;
				case QUARTERLY:
					sessionTotal = rate * 4;
					accruedTotal = sessionTotal;
					break;
				default:
					sessionTotal = rate;
					accruedTotal = rate;
					break;
				}

				double paid = paidMap.getOrDefault(mapping.getId(), 0.0);
				totalPaid += paid;

				double strictDue = Math.max(0, accruedTotal - paid);
				double fullDue = Math.max(0, sessionTotal - paid);

				if (fullDue > 0) {
					FeeHeadDueDTO headDue = new FeeHeadDueDTO();
					headDue.setMappingId(mapping.getId());
					headDue.setFeeHeadName(mapping.getFeeStructure().getFeeHead().getHeadName());
					headDue.setTotalSessionAmount(sessionTotal);
					headDue.setAmountAccruedTillDate(accruedTotal);
					headDue.setPaidAmount(paid);
					headDue.setDueAmountStrict(strictDue);
					headDue.setDueAmountFull(fullDue);

					String paidUpto = "None";
					if (frequency == FeeFrequency.MONTHLY && paid > 0) {
						int covered = (int) (paid / rate);
						if (covered > 0) paidUpto = start.plusMonths(covered - 1).getMonth().toString();
					}
					headDue.setPaidUptoMonth(paidUpto);

					dueList.add(headDue);
					totalStrictDue += strictDue;
					totalFullDue += fullDue;
				}
			}
		}

		report.setDues(dueList);
		report.setTotalFeeAmount(totalStrictDue);
		report.setTotalPaidAmount(totalPaid);
		report.setNetDueAmount(totalStrictDue);

		return report;
	}

	// =================================================================================
	// 1. GET DUES (Single Student)
	// =================================================================================
	@Override
	@Transactional
	public FeeDueReportDTO getDueReport(Long studentId) {
		Student student = studentRepository.findById(studentId)
				.orElseThrow(() -> new RuntimeException("Student not found"));

		StudentEnrollment enrollment = studentEnrollmentRepository.findByStudentIdAndIsCurrentActiveTrue(studentId);
		if (enrollment == null) throw new RuntimeException("No active enrollment found for this student.");

		studentFeeService.assignMandatoryFees(studentId, enrollment.getStandard().getId(), enrollment.getAcademicSession().getId());

		List<StudentFeeMapping> mappings = mappingRepository.findByStudentId(studentId);
		List<FeeTransaction> transactions = transactionRepository.findByStudentIdOrderByTransactionDateDesc(studentId);

		return calculateDues(student, enrollment, mappings, transactions);
	}

	// =================================================================================
	// 2. COLLECT PAYMENT
	// =================================================================================
	@Override
	@Transactional
	public FeeTransaction collectFees(PaymentRequestDTO request, Long collectedByUserId) {
		FeeDueReportDTO dueStatus = getDueReport(request.getStudentId());

		double maxPayableAmount = dueStatus.getDues().stream().mapToDouble(d -> d.getDueAmountFull()).sum();

		if (request.getAmount() > maxPayableAmount + 1.0) {
			throw new RuntimeException("Overpayment not allowed! Max Payable for Session: " + maxPayableAmount);
		}

		FeeTransaction transaction = new FeeTransaction();
		transaction.setStudentId(request.getStudentId());
		transaction.setTotalAmount(request.getAmount());
		transaction.setPaymentMode(request.getPaymentMode());
		transaction.setRemarks(request.getRemarks());
		transaction.setTransactionDate(LocalDateTime.now());

		StudentEnrollment enrollment = studentEnrollmentRepository.findByStudentIdAndIsCurrentActiveTrue(request.getStudentId());
		transaction.setClassId(enrollment.getStandard().getId()); 
		transaction.setAcademicSessionId(enrollment.getAcademicSession().getId());

		User staff = userRepository.findById(collectedByUserId).orElseThrow(() -> new RuntimeException("Staff User not found"));
		transaction.setCollectedBy(staff);

		double remainingMoney = request.getAmount();

		for (FeeHeadDueDTO dueHead : dueStatus.getDues()) {
			if (remainingMoney <= 0) break;

			double amountToPayForThisHead = Math.min(remainingMoney, dueHead.getDueAmountFull());

			if (amountToPayForThisHead > 0) {
				FeeTransactionDetail detail = new FeeTransactionDetail();
				detail.setStudentFeeMapping(mappingRepository.getReferenceById(dueHead.getMappingId()));
				detail.setAmountPaid(amountToPayForThisHead);
				detail.setFeeHeadName(dueHead.getFeeHeadName());
				transaction.addDetail(detail);
				remainingMoney -= amountToPayForThisHead;
			}
		}

		return transactionRepository.save(transaction);
	}

	// =================================================================================
	// 3. TRANSACTION HISTORY
	// =================================================================================
	@Override
	public List<FeeTransactionDTO> getTransactionHistory(Long studentId) {
		List<FeeTransaction> transactions = transactionRepository.findByStudentIdOrderByTransactionDateDesc(studentId);
		return transactions.stream().map(t -> {
			FeeTransactionDTO dto = new FeeTransactionDTO();
			dto.setTransactionId(t.getId());
			dto.setTotalAmount(t.getTotalAmount());
			dto.setPaymentMode(t.getPaymentMode());
			dto.setTransactionDate(t.getTransactionDate());
			dto.setRemarks(t.getRemarks());

			List<FeeTransactionDTO.TransactionDetailItem> details = t.getTransactionDetails().stream().map(d -> {
				FeeTransactionDTO.TransactionDetailItem item = new FeeTransactionDTO.TransactionDetailItem();
				item.setFeeHeadName(d.getFeeHeadName());
				item.setAmount(d.getAmountPaid());
				return item;
			}).collect(Collectors.toList());

			dto.setBreakdown(details);
			return dto;
		}).collect(Collectors.toList());
	}

	// =================================================================================
	// 4. DEFAULTERS LIST (OPTIMIZED - NO MORE N+1 BUGS!)
	// =================================================================================
	@Override
	public List<DefaulterDTO> getDefaultersByClass(Long classId, Long sectionId) {

		List<StudentEnrollment> enrollments;
		if (sectionId != null) {
			enrollments = studentEnrollmentRepository.findByStandardIdAndSectionIdAndIsCurrentActiveTrue(classId, sectionId);
		} else {
			enrollments = studentEnrollmentRepository.findByStandardIdAndIsCurrentActiveTrue(classId);
		}

		List<DefaulterDTO> defaulters = new ArrayList<>();
		if (enrollments.isEmpty()) return defaulters;

		// 1. Extract IDs
		List<Long> studentIds = enrollments.stream().map(e -> e.getStudent().getId()).collect(Collectors.toList());

		// 2. BATCH FETCHING (Only 2 queries sent to DB!)
		List<StudentFeeMapping> allMappings = mappingRepository.findByStudentIdIn(studentIds);
		List<FeeTransaction> allTransactions = transactionRepository.findByStudentIdIn(studentIds);

		// 3. Group by Student in Java Memory
		// Note: If StudentFeeMapping doesn't have a getStudent() method, change m.getStudent().getId() to m.getStudentId()
		Map<Long, List<StudentFeeMapping>> mappingsByStudent = allMappings.stream()
				.collect(Collectors.groupingBy(m -> m.getStudent().getId()));
		
		Map<Long, List<FeeTransaction>> transactionsByStudent = allTransactions.stream()
				.collect(Collectors.groupingBy(FeeTransaction::getStudentId));

		// 4. Process locally
		for (StudentEnrollment enrollment : enrollments) {
			Student student = enrollment.getStudent();
			
			List<StudentFeeMapping> studentMappings = mappingsByStudent.getOrDefault(student.getId(), new ArrayList<>());
			List<FeeTransaction> studentTransactions = transactionsByStudent.getOrDefault(student.getId(), new ArrayList<>());

			// Use our helper instead of triggering DB queries!
			FeeDueReportDTO dueReport = calculateDues(student, enrollment, studentMappings, studentTransactions);

			if (dueReport.getNetDueAmount() > 0) {
				DefaulterDTO dto = new DefaulterDTO();
				dto.setStudentId(student.getId());
				dto.setAdmissionNo(student.getAdmissionNumber());
				dto.setStudentName(student.getFirstName() + " " + student.getLastName());
				dto.setFatherName(student.getFatherName());
				dto.setContactNumber(student.getPrimaryMobile());
				dto.setTotalDueAmount(dueReport.getNetDueAmount());

				defaulters.add(dto);
			}
		}

		defaulters.sort((a, b) -> b.getTotalDueAmount().compareTo(a.getTotalDueAmount()));
		return defaulters;
	}

	// =================================================================================
	// 5. DAILY REPORT
	// =================================================================================
	@Override
	public DailyCollectionReportDTO getDailyCollection(LocalDate date) {
		LocalDateTime startOfDay = date.atStartOfDay();
		LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

		List<FeeTransaction> transactions = transactionRepository.findByTransactionDateBetween(startOfDay, endOfDay);

		double totalCash = 0, totalOnline = 0, totalCheque = 0;

		for (FeeTransaction t : transactions) {
			if (t.getPaymentMode() == PaymentMode.CASH) totalCash += t.getTotalAmount();
			else if (t.getPaymentMode() == PaymentMode.ONLINE) totalOnline += t.getTotalAmount();
			else totalCheque += t.getTotalAmount();
		}

		DailyCollectionReportDTO report = new DailyCollectionReportDTO();
		report.setDate(date.toString());
		report.setTotalCollection(totalCash + totalOnline + totalCheque);
		report.setTotalCash(totalCash);
		report.setTotalOnline(totalOnline);
		report.setTotalCheque(totalCheque);

		List<FeeTransactionDTO> dtoList = transactions.stream().map(t -> {
			FeeTransactionDTO dto = new FeeTransactionDTO();
			dto.setTransactionId(t.getId());
			dto.setTotalAmount(t.getTotalAmount());
			dto.setPaymentMode(t.getPaymentMode());
			dto.setTransactionDate(t.getTransactionDate());
			dto.setRemarks(t.getRemarks());
			return dto;
		}).collect(Collectors.toList());

		report.setTransactions(dtoList);
		return report;
	}

	@Override
	public List<HeadWiseReportDTO> getHeadWiseReport(Long sessionId) {
		return transactionRepository.getHeadWiseCollection(sessionId);
	}
}