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
    private String fatherName;
    private String motherName;
    private String email;
    // ... (Aap baki student fields yahan add kar lena jaise address, aadhar etc.)

    // Master/Relationship IDs
    private Long schoolProfileId;
    private Long standardId;
    private Long sectionId;
    private Long sessionId;
}