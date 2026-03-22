package com.schoolmanagement.schoolbackend.controller;

import com.schoolmanagement.schoolbackend.dto.FeeHeadDTO;
import com.schoolmanagement.schoolbackend.dto.FeeStructureDTO;
import com.schoolmanagement.schoolbackend.dto.FeeTransactionDTO;
import com.schoolmanagement.schoolbackend.service.FeeCollectionService;
import com.schoolmanagement.schoolbackend.service.FeeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/fees")
//@CrossOrigin("*") // Allow access from Angular
public class FeeController {
	
	@Autowired
    private  FeeService feeService;
    
    

//    // Manual Constructor (Since Lombok is acting up)
//    public FeeController(FeeService feeService) {
//        this.feeService = feeService;
//    }

    // --- FEE HEAD ENDPOINTS ---

    // 1. Create a new Fee Type (e.g., "Tuition Fee")
    @PostMapping("/head")
    public ResponseEntity<FeeHeadDTO> createFeeHead(@Valid @RequestBody FeeHeadDTO feeHeadDTO) {
        FeeHeadDTO createdFeeHead = feeService.createFeeHead(feeHeadDTO);
        return new ResponseEntity<>(createdFeeHead, HttpStatus.CREATED);
    }

    // 2. Get all Fee Types (to show in the dropdown list)
    @GetMapping("/head")
    public ResponseEntity<List<FeeHeadDTO>> getAllFeeHeads() {
        return ResponseEntity.ok(feeService.getAllFeeHeads());
    }

    // --- FEE STRUCTURE ENDPOINTS ---

    // 3. Assign a Fee to a Class (e.g., Class 10 Tuition = 5000)
    @PostMapping("/structure")
    public ResponseEntity<FeeStructureDTO> createFeeStructure(@Valid @RequestBody FeeStructureDTO feeStructureDTO) {
        FeeStructureDTO createdStructure = feeService.createFeeStructure(feeStructureDTO);
        return new ResponseEntity<>(createdStructure, HttpStatus.CREATED);
    }

    // 4. Get the full fee breakdown for a specific class & session
    // API Example: /api/fees/structure/101/5 
    // (Where 101 is ClassID and 5 is SessionID)
    @GetMapping("/structure/{classId}/{sessionId}")
    public ResponseEntity<List<FeeStructureDTO>> getFeeStructures(
            @PathVariable Long classId, 
            @PathVariable Long sessionId) {
        
        List<FeeStructureDTO> fees = feeService.getFeeStructuresByClassAndSession(classId, sessionId);
        return ResponseEntity.ok(fees);
    }
    
    @GetMapping("/structure/session/{sessionId}")
    public ResponseEntity<List<FeeStructureDTO>> getFeeStructuresBySession(@PathVariable Long sessionId) {
        return ResponseEntity.ok(feeService.getFeeStructuresBySession(sessionId));
    }
    
 // --- ADD THESE ENDPOINTS ---

    // 5. Get Single Structure (For Edit Screen)
    @GetMapping("/structure/{id}")
    public ResponseEntity<FeeStructureDTO> getFeeStructureById(@PathVariable Long id) {
        return ResponseEntity.ok(feeService.getFeeStructureById(id));
    }

    // 6. Update Structure
    @PutMapping("/structure/{id}")
    public ResponseEntity<FeeStructureDTO> updateFeeStructure(
            @PathVariable Long id, 
            @Valid @RequestBody FeeStructureDTO feeStructureDTO) {
        return ResponseEntity.ok(feeService.updateFeeStructure(id, feeStructureDTO));
    }
    
    
}