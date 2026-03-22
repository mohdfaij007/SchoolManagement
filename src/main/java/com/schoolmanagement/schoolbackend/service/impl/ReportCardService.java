package com.schoolmanagement.schoolbackend.service.impl;

import com.schoolmanagement.schoolbackend.dto.reportcard.ExamResultDTO;
import com.schoolmanagement.schoolbackend.dto.reportcard.ReportCardDTO;
import com.schoolmanagement.schoolbackend.dto.reportcard.SubjectMarkDTO;
import com.schoolmanagement.schoolbackend.model.*;
import com.schoolmanagement.schoolbackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReportCardService {

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ExamRepository examRepository;
    @Autowired
    private ExamSubjectMappingRepository mappingRepository;
    @Autowired
    private StudentMarksRepository marksRepository;
    @Autowired
    private GradeMasterRepository gradeMasterRepository;
    @Autowired
    private AttendanceRepository attendanceRepository;
    
    // NAYA: Enrollment repository inject kiya
    @Autowired
    private StudentEnrollmentRepository studentEnrollmentRepository;

    public ReportCardDTO generateReportCard(Long studentId, List<Long> examIds, Long sessionId) {
        
        // 1. Fetch Student Details
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // NAYA: Fetch current enrollment details
        StudentEnrollment enrollment = studentEnrollmentRepository.findByStudentIdAndIsCurrentActiveTrue(studentId);
        if(enrollment == null) {
        	throw new RuntimeException("No active academic record found for the student.");
        }

        ReportCardDTO reportCard = new ReportCardDTO();
        reportCard.setStudentId(student.getId());
        reportCard.setStudentName(student.getFirstName() + " " + student.getLastName());
        reportCard.setAdmissionNumber(student.getAdmissionNumber());
        
        // UPDATED: Using enrollment to get class, section, and session names
        reportCard.setClassName(enrollment.getStandard().getGradeName());
        reportCard.setSectionName(enrollment.getSection().getSectionName());
        reportCard.setSessionName(enrollment.getAcademicSession().getSessionName());
        
        reportCard.setFatherName(student.getFatherName());
        reportCard.setMotherName(student.getMotherName());
        reportCard.setDateOfBirth(student.getDateOfBirth() != null ? student.getDateOfBirth().toString() : "N/A");
        reportCard.setProfilePhoto(student.getProfilePhoto());

        // 2. Process Exams & Marks
        List<ExamResultDTO> examResults = new ArrayList<>();
        List<GradeMaster> gradingRules = gradeMasterRepository.findAll(); 

        for (Long examId : examIds) {
            Exam exam = examRepository.findById(examId).orElse(null);
            if (exam == null) continue;

            ExamResultDTO examResult = new ExamResultDTO();
            examResult.setExamId(exam.getId());
            examResult.setExamName(exam.getExamName());

            List<SubjectMarkDTO> subjectMarksList = new ArrayList<>();
            Double totalMaxMarks = 0.0;
            Double totalObtained = 0.0;

            // UPDATED: Use enrollment's standard ID
            List<ExamSubjectMapping> mappings = mappingRepository.findByExamIdAndStandardId(exam.getId(), enrollment.getStandard().getId());

            for (ExamSubjectMapping mapping : mappings) {
                SubjectMarkDTO subDto = new SubjectMarkDTO();
                subDto.setSubjectName(mapping.getSubject().getSubjectName());
                subDto.setMaxMarks((double) mapping.getMaxMarks());
                subDto.setPassingMarks((double) mapping.getPassingMarks());

                StudentMarks mark = marksRepository.findByExamSubjectMappingIdAndStudentId(mapping.getId(), student.getId()).orElse(null);

                if (mark != null && !mark.isAbsent() && mark.getMarksObtained() != null) {
                    subDto.setMarksObtained(mark.getMarksObtained());
                    subDto.setRemarks(mark.getRemarks());
                    
                    totalMaxMarks += mapping.getMaxMarks();
                    totalObtained += mark.getMarksObtained();

                    double subPercentage = (mark.getMarksObtained() / mapping.getMaxMarks()) * 100;
                    subDto.setGrade(calculateGrade(subPercentage, gradingRules));
                } else {
                    subDto.setMarksObtained(0.0);
                    subDto.setGrade(mark != null && mark.isAbsent() ? "AB" : "N/A");
                }
                subjectMarksList.add(subDto);
            }

            examResult.setSubjects(subjectMarksList);
            examResult.setTotalMaxMarks(totalMaxMarks);
            examResult.setTotalMarksObtained(totalObtained);

            if (totalMaxMarks > 0) {
                double overallPercentage = (totalObtained / totalMaxMarks) * 100;
                examResult.setPercentage(Math.round(overallPercentage * 100.0) / 100.0); 
                examResult.setOverallGrade(calculateGrade(overallPercentage, gradingRules));
            } else {
                examResult.setPercentage(0.0);
                examResult.setOverallGrade("N/A");
            }

            examResults.add(examResult);
        }
        
        reportCard.setExamResults(examResults);

        // 3. Process Attendance
        List<Attendance> studentAttendance = attendanceRepository.findByStudentIdAndAcademicSessionId(studentId, sessionId);
        
        int presentDays = 0;
        for(Attendance a : studentAttendance) {
            if(a.getStatus().name().equals("PRESENT") || a.getStatus().name().equals("HALFDAY") || a.getStatus().name().equals("LATE")) {
                presentDays++;
            }
        }
        
        reportCard.setTotalWorkingDays(studentAttendance.size());
        reportCard.setPresentDays(presentDays);
        if(studentAttendance.size() > 0) {
            double attPct = ((double) presentDays / studentAttendance.size()) * 100;
            reportCard.setAttendancePercentage(Math.round(attPct * 100.0) / 100.0);
        } else {
            reportCard.setAttendancePercentage(0.0);
        }

        // 4. Set final remarks
        reportCard.setClassTeacherRemarks(generateTeacherRemarks(examResults));

        return reportCard;
    }

    private String calculateGrade(double percentage, List<GradeMaster> gradingRules) {
        for (GradeMaster rule : gradingRules) {
            if (percentage >= rule.getMinPercentage() && percentage <= rule.getMaxPercentage()) {
                return rule.getGradeName();
            }
        }
        return "N/A"; 
    }

    private String generateTeacherRemarks(List<ExamResultDTO> results) {
        if(results.isEmpty() || results.get(0).getPercentage() == null) return "";
        double pct = results.get(results.size()-1).getPercentage(); 
        
        if(pct >= 90) return "Excellent performance. Keep it up!";
        if(pct >= 75) return "Good performance, but can do better.";
        if(pct >= 50) return "Needs to work harder in core subjects.";
        return "Requires immediate attention and parents' meeting.";
    }
}