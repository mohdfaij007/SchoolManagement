package com.schoolmanagement.schoolbackend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@Table(name = "exam_subject_mappings", uniqueConstraints = {
    // Ek class mein ek exam ke andar ek subject do baar assign nahi ho sakta
    @UniqueConstraint(columnNames = {"exam_id", "standard_id", "subject_id"})
})
public class ExamSubjectMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "standard_id", nullable = false)
    private Standard standard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(nullable = false)
    private Double maxMarks;

    @Column(nullable = false)
    private Double passingMarks;

    // Optional: Scheduling data for Time Table generation
    private LocalDate examDate;
    private LocalTime startTime;
    private LocalTime endTime;
}