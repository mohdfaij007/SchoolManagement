package com.schoolmanagement.schoolbackend.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.schoolmanagement.schoolbackend.dto.StudentSummaryDTO;
import com.schoolmanagement.schoolbackend.model.Student;
import com.schoolmanagement.schoolbackend.model.StudentEnrollment;
import com.schoolmanagement.schoolbackend.payload.request.StudentPayloadDTO;
import com.schoolmanagement.schoolbackend.repository.StudentRepository;
import com.schoolmanagement.schoolbackend.repository.StudentEnrollmentRepository;
import com.schoolmanagement.schoolbackend.repository.StandardRepository;
import com.schoolmanagement.schoolbackend.repository.SectionRepository;
import com.schoolmanagement.schoolbackend.repository.AcademicSessionRepository;
import com.schoolmanagement.schoolbackend.repository.SchoolProfileRepository;
import com.schoolmanagement.schoolbackend.service.impl.AdmissionNumberGeneratorService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;
    
    // Naye repositories inject kiye hain
    @Autowired
    private StudentEnrollmentRepository studentEnrollmentRepository;
    @Autowired
    private StandardRepository standardRepository;
    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private AcademicSessionRepository academicSessionRepository;
    @Autowired
    private SchoolProfileRepository schoolProfileRepository;

    private final StudentFeeService studentFeeService;
    private final AdmissionNumberGeneratorService admissionNumberGeneratorService;

    // 1. CREATE (Updated with Enrollment)
    @Transactional 
    public Student saveStudent(StudentPayloadDTO payload) {
        
        // 1. Student ki Core Details Set Karein
        Student student = new Student();
        student.setFirstName(payload.getFirstName());
        student.setLastName(payload.getLastName());
        student.setDateOfBirth(payload.getDateOfBirth());
        student.setContactPhone(payload.getContactPhone());
        student.setFatherName(payload.getFatherName());
        student.setMotherName(payload.getMotherName());
        student.setEmail(payload.getEmail());
        
        // School Profile Set Karein
        if (payload.getSchoolProfileId() != null) {
            student.setSchoolProfile(schoolProfileRepository.findById(payload.getSchoolProfileId())
                .orElseThrow(() -> new RuntimeException("School Profile not found!")));
            
            // Admission Number Generate
            String generatedAdmissionNo = admissionNumberGeneratorService.generateNextAdmissionNumber(payload.getSchoolProfileId());
            student.setAdmissionNumber(generatedAdmissionNo);
        } else {
            throw new RuntimeException("School Profile ID is missing in student payload! Cannot generate admission number.");
        }

        // 2. SAVE STUDENT (Core data)
        Student savedStudent = studentRepository.save(student);

        // 3. ENROLLMENT RECORD BANAO (History ke liye)
        StudentEnrollment enrollment = new StudentEnrollment();
        enrollment.setStudent(savedStudent);
        enrollment.setStandard(standardRepository.findById(payload.getStandardId()).orElseThrow());
        enrollment.setSection(sectionRepository.findById(payload.getSectionId()).orElseThrow());
        enrollment.setAcademicSession(academicSessionRepository.findById(payload.getSessionId()).orElseThrow());
        enrollment.setEnrollmentDate(LocalDate.now());
        enrollment.setCurrentActive(true); // Yeh abhi current class me hai
        
        studentEnrollmentRepository.save(enrollment);

        // 4. THE TRIGGER: Auto-assign fees immediately
        try {
            studentFeeService.assignMandatoryFees(
                savedStudent.getId(), 
                payload.getStandardId(), 
                payload.getSessionId()
            );
        } catch (Exception e) {
            System.err.println("Fee assignment failed for student: " + savedStudent.getId());
            e.printStackTrace();
        }

        return savedStudent;
    }

    // 2. READ ALL
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // 3. READ ONE
    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
    }

    // 4. DELETE
    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    // SEARCH
    public Page<Student> searchStudents(String keyword, Long standardId, Long sectionId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return studentRepository.searchStudents(keyword, standardId, sectionId, pageable);
    }

    // 5. UPDATE (Updated to handle DTO)
    @Transactional
    public Student updateStudent(Long id, StudentPayloadDTO studentDetails) {
        Student student = getStudentById(id);

        // Update core fields
        student.setFirstName(studentDetails.getFirstName());
        student.setLastName(studentDetails.getLastName());
        student.setDateOfBirth(studentDetails.getDateOfBirth());
        student.setContactPhone(studentDetails.getContactPhone());

        // Update current enrollment if Class/Section changed (Optional, depend karta hai aap kaise update karana chahte ho)
        StudentEnrollment currentEnrollment = studentEnrollmentRepository.findByStudentIdAndIsCurrentActiveTrue(id);
        if(currentEnrollment != null) {
            currentEnrollment.setStandard(standardRepository.findById(studentDetails.getStandardId()).orElseThrow());
            currentEnrollment.setSection(sectionRepository.findById(studentDetails.getSectionId()).orElseThrow());
            studentEnrollmentRepository.save(currentEnrollment);
        }

        return studentRepository.save(student);
    }

    // 6. GET STUDENTS BY CLASS (Updated to use Enrollment Repository)
    public List<StudentSummaryDTO> getStudentsByClass(Long standardId, Long sectionId, Long sessionId) {
        
        // Ab data Enrollment table se aayega!
        List<StudentEnrollment> enrollments;
        
     // If the teacher selected a specific section (e.g., 5-A)
        if (sectionId != null) {
            enrollments = studentEnrollmentRepository
                .findByStandardIdAndSectionIdAndAcademicSessionIdAndIsCurrentActiveTrue(standardId, sectionId, sessionId);
        } 
        // If the teacher ONLY selected the class (e.g., all of Class 5)
        else {
            enrollments = studentEnrollmentRepository
                .findByStandardIdAndAcademicSessionIdAndIsCurrentActiveTrue(standardId, sessionId);
        }
        
//        = studentEnrollmentRepository
//            .findByStandardIdAndSectionIdAndAcademicSessionIdAndIsCurrentActiveTrue(standardId, sectionId, sessionId);
//        
        return enrollments.stream().map(enrollment -> {
            Student student = enrollment.getStudent();
            StudentSummaryDTO dto = new StudentSummaryDTO();
            dto.setId(student.getId());
            dto.setAdmissionNo(student.getAdmissionNumber());
            dto.setFullName(student.getFirstName() + " " + student.getLastName()); 
            dto.setFatherName(student.getFatherName());
            return dto;
        }).collect(Collectors.toList());
    }
}