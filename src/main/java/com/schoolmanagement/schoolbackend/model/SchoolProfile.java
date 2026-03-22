package com.schoolmanagement.schoolbackend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "school_profile")
@Data
public class SchoolProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String schoolName;

    @Column(length = 500)
    private String schoolAddress;

    private String contactPhone;
    
    private String contactEmail;
    
    private String website;

    private String affiliationNumber; // e.g., CBSE Affiliation Code

    @Column(name = "logo_path")
    private String logoPath; // School ke logo image ka path ya URL
    
    // Default flag to ensure we only use one active profile
    private boolean isActive = true; 
}