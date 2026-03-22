package com.schoolmanagement.schoolbackend.model;

import com.schoolmanagement.schoolbackend.enums.PaymentMode;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "fee_transactions", indexes = {
    @Index(name = "idx_transaction_student", columnList = "student_id"),
    @Index(name = "idx_transaction_date", columnList = "transaction_date")
})
public class FeeTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //  It will going to ensure that the security  
    @ManyToOne
    @JoinColumn(name = "collected_by_user_id")
    private User collectedBy;
    
    // We store Student ID directly for faster lookups (no need to join Student table every time)
    @Column(name = "student_id", nullable = false)
    private Long studentId;
    
    // We also store Class/Session to lock the receipt to a specific year
    @Column(name = "academic_session_id", nullable = false)
    private Long academicSessionId;

    @Column(name = "class_id", nullable = false)
    private Long classId;

    @Column(nullable = false)
    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_mode", nullable = false)
    private PaymentMode paymentMode;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate = LocalDateTime.now();

    @Column(length = 500)
    private String remarks; // e.g., "Cheque No: 123456"
    
    
    // One Receipt has many breakdown items
    // Cascade ALL: If I delete the receipt, delete the details too.
    @OneToMany(mappedBy = "feeTransaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeeTransactionDetail> transactionDetails = new ArrayList<>();
    
    // Helper method to add details easily
    public void addDetail(FeeTransactionDetail detail) {
        transactionDetails.add(detail);
        detail.setFeeTransaction(this);
    }
}