package com.schoolmanagement.schoolbackend.service.impl;

import com.schoolmanagement.schoolbackend.dto.AcademicCalendarDTO;
import com.schoolmanagement.schoolbackend.enums.HolidayType;
import com.schoolmanagement.schoolbackend.model.AcademicCalendar;
import com.schoolmanagement.schoolbackend.model.AcademicSession;
import com.schoolmanagement.schoolbackend.payload.request.BulkVacationRequest;
import com.schoolmanagement.schoolbackend.repository.AcademicCalendarRepository;
import com.schoolmanagement.schoolbackend.repository.AcademicSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AcademicCalendarService {

    @Autowired
    private AcademicCalendarRepository calendarRepository;

    @Autowired
    private AcademicSessionRepository sessionRepository;

    // 1. Get Calendar for a Session
    public List<AcademicCalendarDTO> getCalendarBySession(Long sessionId) {
        return calendarRepository.findByAcademicSessionIdOrderByDateAsc(sessionId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // 2. Add Single Holiday (Pop-up from Calendar)
    public AcademicCalendarDTO addSingleHoliday(AcademicCalendarDTO dto) {
        if (calendarRepository.existsByAcademicSessionIdAndDate(dto.getAcademicSessionId(), dto.getDate())) {
            throw new RuntimeException("A holiday is already marked on this date!");
        }

        AcademicSession session = sessionRepository.findById(dto.getAcademicSessionId())
                .orElseThrow(() -> new RuntimeException("Session not found"));

        AcademicCalendar calendar = new AcademicCalendar();
        calendar.setAcademicSession(session);
        calendar.setDate(dto.getDate());
        calendar.setDescription(dto.getDescription());
        calendar.setHolidayType(dto.getHolidayType());

        return mapToDTO(calendarRepository.save(calendar));
    }

    // 3. 🌟 MAGIC FEATURE 1: Mark All Sundays Automatically
    @Transactional
    public String markAllSundays(Long sessionId) {
        AcademicSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        // Assuming your session has start and end dates. If not, default to April-March
        LocalDate startDate = session.getStartDate() != null ? session.getStartDate() : LocalDate.of(2025, 4, 1);
        LocalDate endDate = session.getEndDate() != null ? session.getEndDate() : LocalDate.of(2026, 3, 31);

        List<AcademicCalendar> sundaysToSave = new ArrayList<>();
        int count = 0;

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                // Check if already marked to avoid errors
                if (!calendarRepository.existsByAcademicSessionIdAndDate(sessionId, date)) {
                    AcademicCalendar holiday = new AcademicCalendar();
                    holiday.setAcademicSession(session);
                    holiday.setDate(date);
                    holiday.setDescription("Sunday");
                    holiday.setHolidayType(HolidayType.WEEKEND);
                    sundaysToSave.add(holiday);
                    count++;
                }
            }
        }
        
        calendarRepository.saveAll(sundaysToSave);
        return count + " Sundays have been marked as holidays successfully!";
    }

    // 4. 🌟 MAGIC FEATURE 2: Bulk Date Range (e.g., Summer Vacation)
    @Transactional
    public String markVacationRange(BulkVacationRequest request) {
        AcademicSession session = sessionRepository.findById(request.getAcademicSessionId())
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new RuntimeException("Start date cannot be after End date!");
        }

        List<AcademicCalendar> holidaysToSave = new ArrayList<>();
        int count = 0;

        for (LocalDate date = request.getStartDate(); !date.isAfter(request.getEndDate()); date = date.plusDays(1)) {
            if (!calendarRepository.existsByAcademicSessionIdAndDate(session.getId(), date)) {
                AcademicCalendar holiday = new AcademicCalendar();
                holiday.setAcademicSession(session);
                holiday.setDate(date);
                holiday.setDescription(request.getDescription());
                holiday.setHolidayType(request.getHolidayType());
                holidaysToSave.add(holiday);
                count++;
            }
        }

        calendarRepository.saveAll(holidaysToSave);
        return count + " days marked as " + request.getDescription() + "!";
    }

    // 5. Delete Holiday
    public void deleteHoliday(Long id) {
        calendarRepository.deleteById(id);
    }

    private AcademicCalendarDTO mapToDTO(AcademicCalendar entity) {
        AcademicCalendarDTO dto = new AcademicCalendarDTO();
        dto.setId(entity.getId());
        dto.setAcademicSessionId(entity.getAcademicSession().getId());
        dto.setDate(entity.getDate());
        dto.setDescription(entity.getDescription());
        dto.setHolidayType(entity.getHolidayType());
        return dto;
    }
}