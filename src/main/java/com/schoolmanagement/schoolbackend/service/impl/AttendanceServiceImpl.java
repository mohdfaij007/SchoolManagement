package com.schoolmanagement.schoolbackend.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.schoolbackend.dto.AttendanceRecordDTO;
import com.schoolmanagement.schoolbackend.dto.MonthlyRegisterResponseDTO;
import com.schoolmanagement.schoolbackend.enums.HolidayType;
import com.schoolmanagement.schoolbackend.model.AcademicCalendar;
import com.schoolmanagement.schoolbackend.model.AcademicSession;
import com.schoolmanagement.schoolbackend.model.Attendance;
import com.schoolmanagement.schoolbackend.model.AttendanceStatus;
import com.schoolmanagement.schoolbackend.model.Section;
import com.schoolmanagement.schoolbackend.model.Standard;
import com.schoolmanagement.schoolbackend.model.Student;
import com.schoolmanagement.schoolbackend.model.StudentEnrollment;
import com.schoolmanagement.schoolbackend.payload.request.BulkAttendanceRequest;
import com.schoolmanagement.schoolbackend.payload.response.StudentAttendanceSummaryDTO;
import com.schoolmanagement.schoolbackend.repository.AcademicCalendarRepository;
import com.schoolmanagement.schoolbackend.repository.AcademicSessionRepository;
import com.schoolmanagement.schoolbackend.repository.AttendanceRepository;
import com.schoolmanagement.schoolbackend.repository.SectionRepository;
import com.schoolmanagement.schoolbackend.repository.StandardRepository;
import com.schoolmanagement.schoolbackend.repository.StudentRepository;
import com.schoolmanagement.schoolbackend.repository.StudentEnrollmentRepository;
import com.schoolmanagement.schoolbackend.service.AttendanceService;

import com.schoolmanagement.schoolbackend.dto.MonthlyStudentRecordDTO;
import java.time.YearMonth;
import java.util.HashMap;

@Service
public class AttendanceServiceImpl implements AttendanceService {

	@Autowired
	private AttendanceRepository attendanceRepository;
	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private StandardRepository standardRepository;
	@Autowired
	private SectionRepository sectionRepository;
	@Autowired
	private AcademicSessionRepository sessionRepository;

	// NAYA: Enrollment repository for fetching students by class
	@Autowired
	private StudentEnrollmentRepository studentEnrollmentRepository;

	@Autowired
	private AcademicCalendarRepository calendarRepository;

	@Override
	@Transactional
	public String markBulkAttendance(BulkAttendanceRequest request) {

		// --- SMART HOLIDAY CHECK ---
		Optional<AcademicCalendar> holidayOpt = calendarRepository
				.findByAcademicSessionIdAndDate(request.getAcademicSessionId(), request.getDate());
		if (holidayOpt.isPresent() && holidayOpt.get().getHolidayType() != HolidayType.EXAM) {
			throw new RuntimeException(
					"Cannot save. The selected date is a Holiday: " + holidayOpt.get().getDescription());
		}

		Standard standard = standardRepository.findById(request.getStandardId())
				.orElseThrow(() -> new RuntimeException("Standard not found"));
		Section section = sectionRepository.findById(request.getSectionId())
				.orElseThrow(() -> new RuntimeException("Section not found"));
		AcademicSession session = sessionRepository.findById(request.getAcademicSessionId())
				.orElseThrow(() -> new RuntimeException("Session not found"));

		List<Attendance> existingRecords = attendanceRepository.findByStandardIdAndSectionIdAndDate(
				request.getStandardId(), request.getSectionId(), request.getDate());

		Map<Long, Attendance> existingMap = existingRecords.stream()
				.collect(Collectors.toMap(a -> a.getStudent().getId(), a -> a));

		List<Long> studentIds = request.getStudents().stream().map(AttendanceRecordDTO::getStudentId)
				.collect(Collectors.toList());
		Map<Long, Student> studentMap = studentRepository.findAllById(studentIds).stream()
				.collect(Collectors.toMap(Student::getId, s -> s));

		List<Attendance> toSave = new ArrayList<>();

		for (AttendanceRecordDTO dto : request.getStudents()) {
			Student student = studentMap.get(dto.getStudentId());
			if (student == null)
				continue;

			Attendance attendance;
			if (existingMap.containsKey(student.getId())) {
				attendance = existingMap.get(student.getId());
			} else {
				attendance = new Attendance();
				attendance.setStudent(student);
				attendance.setDate(request.getDate());
				attendance.setStandard(standard);
				attendance.setSection(section);
				attendance.setAcademicSession(session);
			}

			attendance.setStatus(dto.getStatus());
			attendance.setRemarks(dto.getRemarks());

			toSave.add(attendance);
		}

		attendanceRepository.saveAll(toSave);

		return "Attendance updated for " + toSave.size() + " students.";
	}

	@Override
	public List<Attendance> getAttendanceByDate(LocalDate date) {
		return null;
	}

	@Override
	public List<AttendanceRecordDTO> getAttendanceForClass(Long stdId, Long secId, LocalDate date) {
		List<Attendance> records = attendanceRepository.findByStandardIdAndSectionIdAndDate(stdId, secId, date);

		return records.stream().map(a -> {
			AttendanceRecordDTO dto = new AttendanceRecordDTO();
			dto.setStudentId(a.getStudent().getId());
			dto.setStudentName(a.getStudent().getFirstName() + " " + a.getStudent().getLastName());
			dto.setAdmissionNo(a.getStudent().getAdmissionNumber());
			dto.setStatus(a.getStatus());
			dto.setRemarks(a.getRemarks());
			return dto;
		}).collect(Collectors.toList());
	}

	public List<StudentAttendanceSummaryDTO> getAttendanceDashboard(Long stdId, Long secId, Long sessId, LocalDate selectedDate) {

		
		// ---  SMART HOLIDAY CHECK  ---
        Optional<AcademicCalendar> holidayOpt = calendarRepository.findByAcademicSessionIdAndDate(sessId, selectedDate);
        if (holidayOpt.isPresent() && holidayOpt.get().getHolidayType() != HolidayType.EXAM) {
            throw new RuntimeException("Attendance cannot be marked. Selected date is a Holiday: " + holidayOpt.get().getDescription());
        }
        
		// --- NAYA LOGIC: Fetch Students using Enrollment Table ---
		List<StudentEnrollment> enrollments = studentEnrollmentRepository
				.findByStandardIdAndSectionIdAndAcademicSessionIdAndIsCurrentActiveTrue(stdId, secId, sessId);

		// Extract students from enrollments
		List<Student> students = enrollments.stream().map(StudentEnrollment::getStudent).collect(Collectors.toList());

		// FIX: Ab pichle 7 din 'selectedDate' se calculate honge, na ki aaj se
        LocalDate sevenDaysAgo = selectedDate.minusDays(7);

		List<Attendance> rangeRecords = attendanceRepository.findByStandardIdAndSectionIdAndDateBetween(stdId, secId,
				sevenDaysAgo, selectedDate);

		return students.stream().map(student -> {
			StudentAttendanceSummaryDTO dto = new StudentAttendanceSummaryDTO();
			dto.setStudentId(student.getId());
			dto.setName(student.getFirstName() + " " + student.getLastName());
			dto.setAdmissionNumber(student.getAdmissionNumber());

			List<String> last7Statuses = rangeRecords.stream()
					.filter(a -> a.getStudent().getId().equals(student.getId())).filter(a -> !a.getDate().equals(selectedDate))
					.sorted(Comparator.comparing(Attendance::getDate)).map(a -> mapStatusToCode(a.getStatus()))
					.collect(Collectors.toList());

			dto.setHistory(last7Statuses);

			String todayStat = rangeRecords.stream()
					.filter(a -> a.getStudent().getId().equals(student.getId()) && a.getDate().equals(selectedDate))
					.map(a -> a.getStatus().name()).findFirst().orElse("PRESENT");

			dto.setTodayStatus(todayStat);
			return dto;

		}).collect(Collectors.toList());
	}

	private String mapStatusToCode(AttendanceStatus status) {
		if (status == null)
			return "-";
		switch (status) {
		case PRESENT:
			return "P";
		case ABSENT:
			return "A";
		case LATE:
			return "L";
		case HALFDAY:
			return "H";
		default:
			return "-";
		}
	}
	
	public MonthlyRegisterResponseDTO getMonthlyRegister(Long classId, Long sectionId, Long sessionId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        MonthlyRegisterResponseDTO response = new MonthlyRegisterResponseDTO();
        response.setYear(year);
        response.setMonth(month);
        response.setDaysInMonth(yearMonth.lengthOfMonth());

        // 1. Fetch Holidays for the Month
        List<AcademicCalendar> holidays = calendarRepository.findByAcademicSessionIdAndDateBetween(sessionId, startDate, endDate);
        Map<Integer, String> holidayMap = new HashMap<>();
        for (AcademicCalendar h : holidays) {
            String code = (h.getHolidayType() == HolidayType.WEEKEND) ? "W" : "HO";
            holidayMap.put(h.getDate().getDayOfMonth(), code);
        }
        response.setHolidayMap(holidayMap);

        // 2. Fetch Students using Enrollment
        List<StudentEnrollment> enrollments = studentEnrollmentRepository
                .findByStandardIdAndSectionIdAndAcademicSessionIdAndIsCurrentActiveTrue(classId, sectionId, sessionId);

        // 3. Fetch Attendance for the Month
        List<Attendance> attendances = attendanceRepository.findByStandardIdAndSectionIdAndDateBetween(
                classId, sectionId, startDate, endDate);

        // 4. Map Attendance to Students
        List<MonthlyStudentRecordDTO> studentRecords = new ArrayList<>();
        for (StudentEnrollment e : enrollments) {
            MonthlyStudentRecordDTO studentDto = new MonthlyStudentRecordDTO();
            studentDto.setStudentId(e.getStudent().getId());
            studentDto.setAdmissionNo(e.getStudent().getAdmissionNumber());
            studentDto.setStudentName(e.getStudent().getFirstName() + " " + e.getStudent().getLastName());

            Map<Integer, String> attendanceMap = new HashMap<>();
            int presentCount = 0;
            int absentCount = 0;

            for (Attendance a : attendances) {
                if (a.getStudent().getId().equals(e.getStudent().getId())) {
                    String status = mapStatusToCode(a.getStatus());
                    attendanceMap.put(a.getDate().getDayOfMonth(), status);
                    
                    if (status.equals("P") || status.equals("L") || status.equals("H")) presentCount++;
                    if (status.equals("A")) absentCount++;
                }
            }

            studentDto.setAttendanceMap(attendanceMap);
            studentDto.setPresentCount(presentCount);
            studentDto.setAbsentCount(absentCount);
            
            studentRecords.add(studentDto);
        }

        // Sort alphabetically
        studentRecords.sort(Comparator.comparing(MonthlyStudentRecordDTO::getStudentName));
        response.setStudents(studentRecords);

        return response;
    }
}