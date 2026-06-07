package com.schoolmanagement.schoolbackend.controller;

import com.schoolmanagement.schoolbackend.dto.GeneratedPaperDTO;
import com.schoolmanagement.schoolbackend.model.GeneratedPaper;
import com.schoolmanagement.schoolbackend.service.impl.GeneratedPaperService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/exams/papers")
@RequiredArgsConstructor
public class GeneratedPaperController {

    private final GeneratedPaperService paperService;

    @PostMapping("/generate")
    public ResponseEntity<?> generateExamPaper(@RequestBody GeneratedPaperDTO payload) {
        try {
            GeneratedPaper savedPaper = paperService.savePaperAndExtractQuestions(payload);
            
            return ResponseEntity.ok(Map.of(
                "message", "Paper saved successfully and questions added to Shadow Bank.",
                "paperId", savedPaper.getId()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
}