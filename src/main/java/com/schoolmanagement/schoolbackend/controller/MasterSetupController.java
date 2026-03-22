package com.schoolmanagement.schoolbackend.controller;

import com.schoolmanagement.schoolbackend.model.SessionClassSetup;
import com.schoolmanagement.schoolbackend.payload.request.SessionClassSetupDTO;
import com.schoolmanagement.schoolbackend.service.impl.MasterSetupService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/master-setup")
public class MasterSetupController {

    @Autowired
    private MasterSetupService masterSetupService;

    @PostMapping("/map-class-sections")
    public ResponseEntity<String> mapClassAndSections(@RequestBody SessionClassSetupDTO dto) {
        String response = masterSetupService.saveClassSectionMapping(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-session/{sessionId}")
    public ResponseEntity<List<SessionClassSetup>> getSetupBySession(@PathVariable Long sessionId) {
        return ResponseEntity.ok(masterSetupService.getSetupBySession(sessionId));
    }
}