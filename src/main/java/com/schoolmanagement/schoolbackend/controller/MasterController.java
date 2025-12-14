package com.schoolmanagement.schoolbackend.controller;

import com.schoolmanagement.schoolbackend.model.AcademicSession;
import com.schoolmanagement.schoolbackend.model.Section;
import com.schoolmanagement.schoolbackend.model.Standard;
import com.schoolmanagement.schoolbackend.repository.AcademicSessionRepository;
import com.schoolmanagement.schoolbackend.repository.SectionRepository;
import com.schoolmanagement.schoolbackend.repository.StandardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/master")
//@CrossOrigin("*") // Angular को एक्सेस देने के लिए
public class MasterController {

    @Autowired
    private AcademicSessionRepository sessionRepo;

    @Autowired
    private StandardRepository standardRepo;

    @Autowired
    private SectionRepository sectionRepo;

    // --- SESSION APIs ---
    @PostMapping("/session/add")
    public ResponseEntity<AcademicSession> addSession(@RequestBody AcademicSession session) {
        return ResponseEntity.ok(sessionRepo.save(session));
    }

    @GetMapping("/session/all")
    public List<AcademicSession> getAllSessions() {
        return sessionRepo.findAll();
    }

    // --- STANDARD (CLASS) APIs ---
    @PostMapping("/standard/add")
    public ResponseEntity<Standard> addStandard(@RequestBody Standard standard) {
        return ResponseEntity.ok(standardRepo.save(standard));
    }

    @GetMapping("/standard/all")
    public List<Standard> getAllStandards() {
        return standardRepo.findAll();
    }

    // --- SECTION APIs ---
    @PostMapping("/section/add")
    public ResponseEntity<Section> addSection(@RequestBody Section section) {
        return ResponseEntity.ok(sectionRepo.save(section));
    }

    @GetMapping("/section/all")
    public List<Section> getAllSections() {
        return sectionRepo.findAll();
    }
}