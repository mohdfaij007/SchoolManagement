package com.schoolmanagement.schoolbackend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "fee_transaction_details")
public class FeeTransactionDetail extends BaseTenantEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link back to the main Receipt
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fee_transaction_id", nullable = false)
    private FeeTransaction feeTransaction;

    // Which specific fee is being paid? (Link to the StudentFeeMapping table)
    // This allows us to calculate "Remaining Due" for this specific fee.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_fee_mapping_id", nullable = false)
    private StudentFeeMapping studentFeeMapping;

    @Column(nullable = false)
    private Double amountPaid; 
    
    // e.g. "Tuition Fee (April)" - Storing name here avoids extra Joins in reports
    @Column(name = "fee_head_name") 
    private String feeHeadName; 
}