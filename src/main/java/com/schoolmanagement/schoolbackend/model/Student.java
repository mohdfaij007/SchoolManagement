package com.schoolmanagement.schoolbackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.schoolmanagement.schoolbackend.enums.Gender;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = {
    // Ab Admission sirf ek particular school ke andar unique rahega
    @UniqueConstraint(columnNames = {"admissionNumber", "school_profile_id"})
})
public class Student extends BaseTenantEntity{
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;

    private LocalDate dateOfBirth;

    // A unique identification number (e.g., admission number)
    @Column( nullable = false)
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
    
    @Enumerated(EnumType.STRING)
    private Gender gender;
    
 // --- NEW LINKS TO MASTERS ---

    // Link to Standard (Class) - Many Students can be in One Standard
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnoreProperties("student") // YEH ZAROORI HAI: Taaki loop na bane
    private List<StudentEnrollment> enrollments = new ArrayList<>();
    
    @Column(name = "profile_photo_path")
    private String profilePhoto;
    
    

    
    
    
  
    
}
