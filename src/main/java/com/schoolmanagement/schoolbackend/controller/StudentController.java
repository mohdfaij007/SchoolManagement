package com.schoolmanagement.schoolbackend.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.schoolmanagement.schoolbackend.dto.StudentSummaryDTO;
import com.schoolmanagement.schoolbackend.model.Student;
import com.schoolmanagement.schoolbackend.payload.request.StudentPayloadDTO;
import com.schoolmanagement.schoolbackend.repository.StudentRepository;
import com.schoolmanagement.schoolbackend.service.StudentService;
import com.schoolmanagement.schoolbackend.service.impl.FileStorageService;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentService studentService;
    
    @Autowired
    private FileStorageService fileStorageService;
    
 // Added to save the entity directly and avoid the DTO null-overwrite bug
    @Autowired
    private StudentRepository studentRepository;

    // POST: Create a Student (UPDATED TO USE DTO)
    @PostMapping
    public ResponseEntity<Student> saveStudent(@RequestBody StudentPayloadDTO payload) {
        Student savedStudent = studentService.saveStudent(payload);
        return new ResponseEntity<>(savedStudent, HttpStatus.CREATED);
    }

    // GET: Retrieve all Students
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        List<Student> students = studentService.getAllStudents();
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    // GET Single Student
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudent(@PathVariable Long id) {
        return new ResponseEntity<>(studentService.getStudentById(id), HttpStatus.OK);
    }
    
    // UPDATE Student (UPDATED TO USE DTO)
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id, @RequestBody StudentPayloadDTO payload) {
        return new ResponseEntity<>(studentService.updateStudent(id, payload), HttpStatus.OK);
    }
    
 // UPDATE PHOTO (FIXED: Cloudinary integration + Data Corruption Bug Resolved)
    @PostMapping("/{id}/photo")
    public ResponseEntity<?> uploadPhoto(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            // 1. Upload to Cloudinary using the new method in FileStorageService
            String photoUrl = fileStorageService.uploadImage(file);

            // 2. Fetch the existing student
            Student student = studentService.getStudentById(id);

            // 3. Update ONLY the profile photo URL field
            student.setProfilePhoto(photoUrl);

            // 4. Save directly via repository. 
            // This prevents the empty StudentPayloadDTO from overwriting other fields with nulls.
            studentRepository.save(student);

            // 5. Return a JSON object so Angular can easily extract the URL
            return ResponseEntity.ok(Map.of(
                "message", "Photo uploaded successfully",
                "url", photoUrl
            ));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload image to cloud storage."));
        }
    }
    // DELETE: Delete a Student
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // SEARCH
    @GetMapping("/search")
    public ResponseEntity<Page<Student>> searchStudents(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "standardId", required = false) Long standardId,
            @RequestParam(value = "sectionId", required = false) Long sectionId,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        Page<Student> students = studentService.searchStudents(keyword, standardId, sectionId, page, size);
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    @GetMapping("/by-class")
    public ResponseEntity<List<StudentSummaryDTO>> getStudentsByClass(
            @RequestParam Long classId,
            @RequestParam Long sectionId,
            @RequestParam Long sessionId) {
        
        return ResponseEntity.ok(studentService.getStudentsByClass(classId, sectionId, sessionId));
    }
}