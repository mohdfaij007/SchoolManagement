package com.schoolmanagement.schoolbackend.controller;

import com.schoolmanagement.schoolbackend.dto.GradeMasterDTO;
import com.schoolmanagement.schoolbackend.service.impl.GradeMasterService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grades")
public class GradeMasterController {

    @Autowired
    private GradeMasterService gradeService;

    // 1. Create New Grade Rule
    @PostMapping
    public ResponseEntity<?> createGrade(@Valid @RequestBody GradeMasterDTO dto) {
        try {
            GradeMasterDTO createdGrade = gradeService.createGrade(dto);
            return new ResponseEntity<>(createdGrade, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 2. Get All Grades
    @GetMapping
    public ResponseEntity<List<GradeMasterDTO>> getAllGrades() {
        return ResponseEntity.ok(gradeService.getAllGrades());
    }
}