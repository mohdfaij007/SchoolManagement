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

	// 1. GET DUES (The "Bill")
	@Override
	@Transactional
	public FeeDueReportDTO getDueReport(Long studentId) {

		// 1. Self-Healing (Ghost Fees)
		Student student = studentRepository.findById(studentId)
				.orElseThrow(() -> new RuntimeException("Student not found"));

		// NAYA: Student ka current enrollment fetch karo
		StudentEnrollment enrollment = studentEnrollmentRepository.findByStudentIdAndIsCurrentActiveTrue(studentId);
		if (enrollment == null) {
			throw new RuntimeException("No active enrollment found for this student.");
		}

		studentFeeService.assignMandatoryFees(studentId, enrollment.getStandard().getId(),
				enrollment.getAcademicSession().getId());

		List<StudentFeeMapping> mappings = mappingRepository.findByStudentId(studentId);
		List<FeeTransaction> transactions = transactionRepository.findByStudentIdOrderByTransactionDateDesc(studentId);

		// 2. Build Paid Map
		Map<Long, Double> paidMap = new HashMap<>();
		for (FeeTransaction t : transactions) {
			for (FeeTransactionDetail d : t.getTransactionDetails()) {
				Long mapId = d.getStudentFeeMapping().getId();
				paidMap.put(mapId, paidMap.getOrDefault(mapId, 0.0) + d.getAmountPaid());
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

		// Dates
		LocalDate today = LocalDate.now();
		// Force "End of Current Month" as the strict cut-off.
		// If today is Jan 10, we charge for full Jan.
		LocalDate strictCutOff = today.withDayOfMonth(today.lengthOfMonth());

		// Session Dates
		LocalDate sessionStart = LocalDate.now().withMonth(4).withDayOfMonth(1);
		if (today.getMonthValue() < 4)
			sessionStart = LocalDate.of(today.getYear() - 1, 4, 1);
		LocalDate sessionEnd = sessionStart.plusYears(1).minusDays(1);

		if (enrollment.getAcademicSession() != null && enrollment.getAcademicSession().getStartDate() != null) {
			sessionStart = enrollment.getAcademicSession().getStartDate();
			sessionEnd = enrollment.getAcademicSession().getEndDate();
		}

		for (StudentFeeMapping mapping : mappings) {
			if (!mapping.isActive())
				continue; // Skip inactive (unless we want to collect past dues)

			com.schoolmanagement.schoolbackend.enums.FeeFrequency frequency = mapping.getFeeStructure().getFeeHead()
					.getFrequency();
			double rate = mapping.getFeeStructure().getAmount();

			// A. Determine Effective Dates
			LocalDate start = (mapping.getStartDate() != null) ? mapping.getStartDate() : sessionStart;
			LocalDate end = (mapping.getEndDate() != null) ? mapping.getEndDate() : sessionEnd;

			// Clamp
			if (start.isBefore(sessionStart))
				start = sessionStart;
			if (end.isAfter(sessionEnd))
				end = sessionEnd;

			// B. Calculate TWO Amounts
			double sessionTotal = 0; // Total Liability (Apr-Mar)
			double accruedTotal = 0; // Strict Liability (Apr-Today)

			switch (frequency) {
			case MONTHLY:
				// 1. Full Session
				long totalMonths = java.time.temporal.ChronoUnit.MONTHS.between(start.withDayOfMonth(1),
						end.withDayOfMonth(1)) + 1;
				sessionTotal = rate * totalMonths;

				// 2. Strict (Till Today)
				LocalDate strictEnd = end.isBefore(strictCutOff) ? end : strictCutOff;
				if (!strictEnd.isBefore(start)) {
					long accruedMonths = java.time.temporal.ChronoUnit.MONTHS.between(start.withDayOfMonth(1),
							strictEnd.withDayOfMonth(1)) + 1;
					accruedTotal = rate * accruedMonths;
				}
				break;

			case QUARTERLY:
				// Similar logic for Quarters...
				// For simplicity, treating Quarterly same as Annual for strictness or calculate
				// quarters passed.
				// Let's assume Annual/OneTime are due immediately.
				sessionTotal = rate * 4; // Approx
				accruedTotal = sessionTotal; // Usually due in advance
				break;

			default: // ANNUALLY / ONE_TIME
				sessionTotal = rate;
				accruedTotal = rate; // Always due immediately
				break;
			}

			double paid = paidMap.getOrDefault(mapping.getId(), 0.0);
			totalPaid += paid;

			// C. Calculate Dues
			double strictDue = Math.max(0, accruedTotal - paid);
			double fullDue = Math.max(0, sessionTotal - paid);

			// Add to list if there is ANY due (Strict OR Future)
			if (fullDue > 0) {
				FeeHeadDueDTO headDue = new FeeHeadDueDTO();
				headDue.setMappingId(mapping.getId());
				headDue.setFeeHeadName(mapping.getFeeStructure().getFeeHead().getHeadName());

				headDue.setTotalSessionAmount(sessionTotal);
				headDue.setAmountAccruedTillDate(accruedTotal);
				headDue.setPaidAmount(paid);

				headDue.setDueAmountStrict(strictDue); // 👈 Default this in Frontend Input
				headDue.setDueAmountFull(fullDue); // 👈 Show this as "Total Remaining"

				// Paid Upto Logic
				String paidUpto = "None";
				if (frequency == com.schoolmanagement.schoolbackend.enums.FeeFrequency.MONTHLY && paid > 0) {
					int covered = (int) (paid / rate);
					if (covered > 0)
						paidUpto = start.plusMonths(covered - 1).getMonth().toString();
				}
				headDue.setPaidUptoMonth(paidUpto);

				dueList.add(headDue);

				totalStrictDue += strictDue;
				totalFullDue += fullDue;
			}
		}

		report.setDues(dueList);
		// We now have two totals. You can decide which one to put in the main report
		// fields.
		// Usually, 'NetDueAmount' is the Strict Due.
		report.setTotalFeeAmount(totalStrictDue); // "Current Due"
		report.setTotalPaidAmount(totalPaid);
		report.setNetDueAmount(totalStrictDue);

		// Optional: Add a new field to FeeDueReportDTO for 'totalFutureLiability' if
		// needed
		// report.setTotalFutureLiability(totalFullDue);

		return report;
	}

	// 2. COLLECT PAYMENT
	@Override
	@Transactional
	public FeeTransaction collectFees(PaymentRequestDTO request, Long collectedByUserId) {

		// 1. Get the Report (Contains both Strict & Full Dues)
		FeeDueReportDTO dueStatus = getDueReport(request.getStudentId());

		// 👇 CHANGE 1: Calculate the Maximum Possible Payment (Full Year)
		// We cannot rely on dueStatus.getNetDueAmount() because that now shows only
		// "Strict Due".
		double maxPayableAmount = dueStatus.getDues().stream().mapToDouble(d -> d.getDueAmountFull()) // Sum of all
																										// Future Dues
				.sum();

		// Allow a small buffer for floating point errors (optional, but good practice)
		if (request.getAmount() > maxPayableAmount + 1.0) {
			throw new RuntimeException("Overpayment not allowed! Max Payable for Session: " + maxPayableAmount);
		}

		FeeTransaction transaction = new FeeTransaction();
		transaction.setStudentId(request.getStudentId());
		transaction.setTotalAmount(request.getAmount());
		transaction.setPaymentMode(request.getPaymentMode());
		transaction.setRemarks(request.getRemarks());

		transaction.setTransactionDate(LocalDateTime.now());

		Student student = studentRepository.findById(request.getStudentId())
				.orElseThrow(() -> new RuntimeException("Student not found"));

		StudentEnrollment enrollment = studentEnrollmentRepository
				.findByStudentIdAndIsCurrentActiveTrue(request.getStudentId());
		transaction.setClassId(enrollment.getStandard().getId()); // UPDATED
		transaction.setAcademicSessionId(enrollment.getAcademicSession().getId()); // UPDATED

		User staff = userRepository.findById(collectedByUserId)
				.orElseThrow(() -> new RuntimeException("Staff User not found"));
		transaction.setCollectedBy(staff);

		// Auto-Allocation Logic
		double remainingMoney = request.getAmount();

		for (FeeHeadDueDTO dueHead : dueStatus.getDues()) {
			if (remainingMoney <= 0)
				break;

			// 👇 CHANGE 2: Allocate based on FULL Due, not Strict Due
			// If we used 'getDueAmountStrict', the system would reject money meant for
			// Feb/March.
			// Using 'getDueAmountFull' allows filling the bucket for the whole year.
			double amountToPayForThisHead = Math.min(remainingMoney, dueHead.getDueAmountFull());

			if (amountToPayForThisHead > 0) {
				FeeTransactionDetail detail = new FeeTransactionDetail();
				// Handle "Ghost Fees" (mappingId -1) if necessary, or assume self-healing fixed
				// it.
				// Since we added Self-Healing to getDueReport, mappingId should be valid here.
				detail.setStudentFeeMapping(mappingRepository.getReferenceById(dueHead.getMappingId()));
				detail.setAmountPaid(amountToPayForThisHead);
				detail.setFeeHeadName(dueHead.getFeeHeadName());

				transaction.addDetail(detail);

				remainingMoney -= amountToPayForThisHead;
			}
		}

		return transactionRepository.save(transaction);
	}

	// 3. TRANSACTION HISTORY
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

			// Map breakdown
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

	// 4. DEFAULTERS LIST
	@Override
	public List<DefaulterDTO> getDefaultersByClass(Long classId, Long sectionId) {

		List<StudentEnrollment> enrollments;

		// --- FIX: Ab hum null session pass karne ki jagah naye methods call kar rahe
		// hain ---
		if (sectionId != null) {
			enrollments = studentEnrollmentRepository.findByStandardIdAndSectionIdAndIsCurrentActiveTrue(classId,
					sectionId);
		} else {
			enrollments = studentEnrollmentRepository.findByStandardIdAndIsCurrentActiveTrue(classId);
		}

		List<DefaulterDTO> defaulters = new ArrayList<>();

		for (StudentEnrollment enrollment : enrollments) {
			Student student = enrollment.getStudent();
			FeeDueReportDTO dueReport = getDueReport(student.getId());

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

	// 5. DAILY REPORT
	@Override
	public DailyCollectionReportDTO getDailyCollection(LocalDate date) {
		LocalDateTime startOfDay = date.atStartOfDay();
		LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

		List<FeeTransaction> transactions = transactionRepository.findByTransactionDateBetween(startOfDay, endOfDay);

		// Optimization: Calculate totals in a single pass if preferred, but Streams are
		// readable
		double totalCash = 0;
		double totalOnline = 0;
		double totalCheque = 0;

		for (FeeTransaction t : transactions) {
			if (t.getPaymentMode() == PaymentMode.CASH)
				totalCash += t.getTotalAmount();
			else if (t.getPaymentMode() == PaymentMode.ONLINE)
				totalOnline += t.getTotalAmount();
			else
				totalCheque += t.getTotalAmount();
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