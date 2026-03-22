package com.schoolmanagement.schoolbackend.controller;

import com.schoolmanagement.schoolbackend.dto.DailyCollectionReportDTO;
import com.schoolmanagement.schoolbackend.dto.DefaulterDTO;
import com.schoolmanagement.schoolbackend.dto.FeeDueReportDTO;
import com.schoolmanagement.schoolbackend.dto.FeeTransactionDTO;
import com.schoolmanagement.schoolbackend.dto.HeadWiseReportDTO;
import com.schoolmanagement.schoolbackend.dto.PaymentRequestDTO;
import com.schoolmanagement.schoolbackend.model.FeeTransaction;
import com.schoolmanagement.schoolbackend.model.User;
import com.schoolmanagement.schoolbackend.repository.UserRepository;
import com.schoolmanagement.schoolbackend.security.CustomUserDetailsService;
import com.schoolmanagement.schoolbackend.service.FeeCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fees/collection")
//@CrossOrigin("*")
public class FeeCollectionController {

    @Autowired
    private FeeCollectionService feeCollectionService;
    @Autowired
    private UserRepository userRepository;
    

    // 1. GET DUES: Load this when opening the "Pay Fee" screen
    // URL: GET /api/fees/collection/due/5 (for student ID 5)
    @GetMapping("/due/{studentId}")
    public ResponseEntity<FeeDueReportDTO> getStudentDues(@PathVariable Long studentId) {
        return ResponseEntity.ok(feeCollectionService.getDueReport(studentId));
    }

    // 2. PAY FEES: Click "Collect & Print"
    // URL: POST /api/fees/collection/pay
    @PostMapping("/pay")
    public ResponseEntity<?> collectFees(@RequestBody PaymentRequestDTO request,Authentication authentication) {
        try {
        	
//        	// 1. Get the username/ID of the logged-in staff
//            CustomUserDetailsService userDetails = (CustomUserDetailsService) authentication.getPrincipal();
//            
//            System.out.println(userDetails);
////            Long staffId = userDetails.getId();
//            Long staffId=(long) 1;
        	
        	String username = authentication.getName(); // Get username safely
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Authenticated user not found in DB"));
            Long staffId = user.getId();
            FeeTransaction transaction = feeCollectionService.collectFees(request,staffId);
            
            // Return the Transaction ID so frontend can generate a receipt URL
            return ResponseEntity.ok(Map.of(
                "message", "Payment Successful",
                "transactionId", transaction.getId(),
                "amountPaid", transaction.getTotalAmount()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
 // 3. GET HISTORY
    @GetMapping("/history/{studentId}")
    public ResponseEntity<List<FeeTransactionDTO>> getStudentHistory(@PathVariable Long studentId) {
        return ResponseEntity.ok(feeCollectionService.getTransactionHistory(studentId));
    }
    
    
    
 // 4. GET DEFAULTERS LIST
    @GetMapping("/defaulters")
    public ResponseEntity<List<DefaulterDTO>> getDefaulters(
            @RequestParam Long classId,
            @RequestParam(required = false) Long sectionId) {
        return ResponseEntity.ok(feeCollectionService.getDefaultersByClass(classId, sectionId));
    }
    
 // 5. GET DAILY COLLECTION REPORT
    @GetMapping("/daily-report")
    public ResponseEntity<DailyCollectionReportDTO> getDailyReport(
            @RequestParam(required = false) String date) {
        
        // Default to TODAY if no date provided
        LocalDate reportDate = (date != null) ? LocalDate.parse(date) : LocalDate.now();
        
        return ResponseEntity.ok(feeCollectionService.getDailyCollection(reportDate));
    }
    
 // 6. GET HEAD-WISE COLLECTION REPORT
    @GetMapping("/head-wise-report")
    public ResponseEntity<List<HeadWiseReportDTO>> getHeadWiseReport(@RequestParam Long sessionId) {
        return ResponseEntity.ok(feeCollectionService.getHeadWiseReport(sessionId));
    }
}