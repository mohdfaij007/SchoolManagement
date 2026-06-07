package com.schoolmanagement.schoolbackend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "student_enrollments")
public class StudentEnrollment extends BaseTenantEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnoreProperties("enrollments")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    private AcademicSession academicSession;

    @ManyToOne
    @JoinColumn(name = "standard_id", nullable = false)
    private Standard standard;

    @ManyToOne
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    private LocalDate enrollmentDate;
    
    // ACTIVE flag batayega ki bachha abhi is session mein padh raha hai ya pass ho chuka hai
    private boolean isCurrentActive = true; 
}