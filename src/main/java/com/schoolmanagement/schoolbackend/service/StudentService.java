package com.schoolmanagement.schoolbackend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.schoolmanagement.schoolbackend.model.Student;
import com.schoolmanagement.schoolbackend.repository.StudentRepository;

@Service
public class StudentService {
	@Autowired
	private StudentRepository studentRepository;
	
	// 1. CREATE/UPDATE
    public Student saveStudent(Student student) {
        // Here you could add validation logic (e.g., check if admission number already exists)
        return studentRepository.save(student);
    }

    // 2. READ ALL
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // 3. READ ONE
    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }
    
 // 4. DELETE
    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }
}
