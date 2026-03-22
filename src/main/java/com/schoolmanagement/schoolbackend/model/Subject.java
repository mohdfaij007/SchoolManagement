package com.schoolmanagement.schoolbackend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "subjects")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String subjectName; // e.g., "Mathematics", "Hindi"

    private String subjectCode; // e.g., "MAT-101" (Optional)
    
    // Type of subject (e.g., "THEORY", "PRACTICAL", "CO-SCHOLASTIC")
    @Column(nullable = false, columnDefinition = "varchar(255) default 'THEORY'")
    private String subjectType = "THEORY"; 
}