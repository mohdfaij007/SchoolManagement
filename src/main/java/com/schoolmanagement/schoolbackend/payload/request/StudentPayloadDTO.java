package com.schoolmanagement.schoolbackend.payload.request;

import java.time.LocalDate;
import lombok.Data;

@Data
public class StudentPayloadDTO {
    // Basic Details
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String contactPhone;
    private String gender;
    
    
    private String fatherName;
    private String motherName;
    private String email;
    
    //
    
    private String nationality;
    private String aadharNumber;
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
    
    // ... (Aap baki student fields yahan add kar lena jaise address, aadhar etc.)

    // Master/Relationship IDs
    private Long schoolProfileId;
    private Long standardId;
    private Long sectionId;
    private Long sessionId;
}