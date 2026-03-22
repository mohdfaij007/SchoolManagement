package com.schoolmanagement.schoolbackend.dto;

import com.schoolmanagement.schoolbackend.enums.HolidayType;
import lombok.Data;
import java.time.LocalDate;

@Data
public class AcademicCalendarDTO {
    private Long id;
    private Long academicSessionId;
    private LocalDate date;
    private String description;
    private HolidayType holidayType;
}