package com.schoolmanagement.schoolbackend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "school_configuration")
@Data
public class SchoolConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Har configuration strictly ek school se judi hogi
    @OneToOne
    @JoinColumn(name = "school_profile_id", nullable = false)
    private SchoolProfile schoolProfile;

    // Example: "DPS-[YYYY]-[SEQ]"
    @Column(nullable = false)
    private String admissionPattern;

    // Last generated number (e.g., 105)
    @Column(nullable = false)
    private Integer currentSequence;

    // Kitne zero lagane hain (e.g., 4 = 0105)
    @Column(nullable = false)
    private Integer sequencePadding; 
}