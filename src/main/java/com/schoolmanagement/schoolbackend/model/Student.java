package com.schoolmanagement.schoolbackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;

    private LocalDate dateOfBirth;

    // A unique identification number (e.g., admission number)
    @Column(unique = true, nullable = false)
    private String admissionNumber;

//    private String grade;
    
    private String contactPhone;
    
    private String fatherName;
    
    
    private String nationality;
    
    private String aadharNumber;
    
    private String email;
    
    private String motherName;
    
    private String fatherOccupation;
    
    private String motherOccupation;
    
    private String primaryMobile;
    
    private String secondaryMobile;
    
    private String currentAddres;
    
    private String city;
    
    private String state;
    
    private String pincode;
    
    private Boolean isAddressSame;
    
    private String permanentAddress;
    
    private String prevSchoolName;
    
    private String lastClassPassed;
    
    private String prevGrade;
    
    private String tcNumber;
    
    
 // --- NEW LINKS TO MASTERS ---

    // Link to Standard (Class) - Many Students can be in One Standard
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnoreProperties("student") // YEH ZAROORI HAI: Taaki loop na bane
    private List<StudentEnrollment> enrollments = new ArrayList<>();
    
    @Column(name = "profile_photo_path")
    private String profilePhoto;
    
    
 // --- MULTI-TENANT LINK ---
    @ManyToOne
    @JoinColumn(name = "school_profile_id")
    private SchoolProfile schoolProfile;
    
    
    
    
  
    
}
