package com.schoolmanagement.schoolbackend.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "generated_papers")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GeneratedPaper extends BaseTenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    

    // Optional: Links to your existing "Term 1" or "Term 2" exam definitions
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id")
    private Exam exam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_session_id", nullable = false)
    private AcademicSession academicSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "standard_id", nullable = false)
    private Standard standard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(nullable = false)
    private String title; // e.g., "2026 Mid-Term Physics Set A"

    @Column(name = "total_marks", nullable = false)
    private Integer totalMarks;

    @Column(name = "duration_minutes")
    private Integer durationMinutes; // e.g., 180 (for 3 hours)

    // Maps to our custom join table below
    @OneToMany(mappedBy = "generatedPaper", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GeneratedPaperQuestion> paperQuestions = new ArrayList<>();
    
    // Helper method to add questions cleanly
    public void addPaperQuestion(GeneratedPaperQuestion paperQuestion) {
        paperQuestions.add(paperQuestion);
        paperQuestion.setGeneratedPaper(this);
    }
}