package com.schoolmanagement.schoolbackend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "session_class_setup", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"session_id", "standard_id", "section_id"})
})
public class SessionClassSetup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    private AcademicSession academicSession;

    @ManyToOne
    @JoinColumn(name = "standard_id", nullable = false)
    private Standard standard;

    @ManyToOne
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    // Optional: Enterprise feature for admission limits
    @Column(name = "max_capacity")
    private Integer maxCapacity = 40; 
}