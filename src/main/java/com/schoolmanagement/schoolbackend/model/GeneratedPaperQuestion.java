package com.schoolmanagement.schoolbackend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "generated_paper_questions", uniqueConstraints = {
    // Ensures the same question isn't added twice to the exact same paper
    @UniqueConstraint(columnNames = {"generated_paper_id", "question_id"})
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class GeneratedPaperQuestion extends BaseTenantEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generated_paper_id", nullable = false)
    private GeneratedPaper generatedPaper;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "question_order", nullable = false)
    private Integer questionOrder; // e.g., 1 for Q1, 2 for Q2

    @Column(name = "assigned_marks", nullable = false)
    private Integer assignedMarks; // Allows overriding the default question marks for a specific paper
}