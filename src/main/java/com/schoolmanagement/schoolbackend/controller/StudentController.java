package com.schoolmanagement.schoolbackend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.schoolmanagement.schoolbackend.dto.StudentSummaryDTO;
import com.schoolmanagement.schoolbackend.model.Student;
import com.schoolmanagement.schoolbackend.payload.request.StudentPayloadDTO;
import com.schoolmanagement.schoolbackend.service.FileStorageService;
import com.schoolmanagement.schoolbackend.service.StudentService;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentService studentService;
    
    @Autowired
    private FileStorageService fileStorageService;

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
    
    @PostMapping("/{id}/photo")
    public ResponseEntity<String> uploadPhoto(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        String filename = fileStorageService.saveFile(file);
        
        Student student = studentService.getStudentById(id);
        student.setProfilePhoto(filename);
        
        // Save the direct entity update for photo
        studentService.updateStudent(id, new StudentPayloadDTO()); // Note: Isko directly repo.save(student) se bhi kar sakte hain photo ke case me
        
        return ResponseEntity.ok("Photo uploaded successfully: " + filename);
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