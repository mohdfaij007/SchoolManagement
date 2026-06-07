package com.schoolmanagement.schoolbackend.model;

import com.schoolmanagement.schoolbackend.enums.HolidayType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "academic_calendar", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"academic_session_id", "date"})
})
public class AcademicCalendar extends BaseTenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_session_id", nullable = false)
    private AcademicSession academicSession;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String description; // e.g., "Diwali", "Summer Vacation"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private HolidayType holidayType;
}