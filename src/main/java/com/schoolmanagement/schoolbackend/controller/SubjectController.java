package com.schoolmanagement.schoolbackend.controller;

import com.schoolmanagement.schoolbackend.dto.SubjectDTO;
import com.schoolmanagement.schoolbackend.service.impl.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    // 1. Add a new Subject (Standard: Accepts DTO, Returns DTO)
    @PostMapping
    public ResponseEntity<?> createSubject(@RequestBody SubjectDTO subjectDTO) {
        try {
            SubjectDTO createdSubject = subjectService.createSubject(subjectDTO);
            return new ResponseEntity<>(createdSubject, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // Agar duplicate subject aaya toh yahan catch hoga
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 2. Get all Subjects
    @GetMapping
    public ResponseEntity<List<SubjectDTO>> getAllSubjects() {
        return ResponseEntity.ok(subjectService.getAllSubjects());
    }
}