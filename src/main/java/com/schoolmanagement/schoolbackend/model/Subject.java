package com.schoolmanagement.schoolbackend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "subjects", uniqueConstraints = {
	    // Ab Subject Name sirf ek particular school ke andar unique rahega
	    @UniqueConstraint(columnNames = {"gradeName", "school_profile_id"})
	})
public class Subject extends BaseTenantEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String subjectName; // e.g., "Mathematics", "Hindi"

    private String subjectCode; // e.g., "MAT-101" (Optional)
    
    // Type of subject (e.g., "THEORY", "PRACTICAL", "CO-SCHOLASTIC")
    @Column(nullable = false, columnDefinition = "varchar(255) default 'THEORY'")
    private String subjectType = "THEORY"; 
}