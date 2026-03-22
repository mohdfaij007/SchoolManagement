package com.schoolmanagement.schoolbackend.payload.request;

import com.schoolmanagement.schoolbackend.enums.HolidayType;
import lombok.Data;
import java.time.LocalDate;

@Data
public class BulkVacationRequest {
    private Long academicSessionId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description; // e.g., "Summer Vacation"
    private HolidayType holidayType; // e.g., VACATION
}