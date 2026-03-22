package com.schoolmanagement.schoolbackend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "student_marks", uniqueConstraints = {
    // Ek bachhe ka ek exam-subject mein ek hi mark ho sakta hai
    @UniqueConstraint(columnNames = {"exam_subject_mapping_id", "student_id"})
})
@Data
@NoArgsConstructor
public class StudentMarks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_subject_mapping_id", nullable = false)
    private ExamSubjectMapping examSubjectMapping;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "marks_obtained")
    private Double marksObtained;

    @Column(name = "is_absent")
    private boolean isAbsent = false;

    @Column(name = "remarks", length = 200)
    private String remarks;
}