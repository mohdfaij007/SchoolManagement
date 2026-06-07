package com.schoolmanagement.schoolbackend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "grade_masters")
public class GradeMaster extends BaseTenantEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String gradeName; // e.g., "A1", "B2"

    @Column(nullable = false)
    private Double minPercentage; // e.g., 91.0

    @Column(nullable = false)
    private Double maxPercentage; // e.g., 100.0

    private Double gradePoint; // e.g., 10.0 (For CGPA calculation)

    private String remarks; // e.g., "Outstanding", "Needs Improvement"
}