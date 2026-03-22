package com.schoolmanagement.schoolbackend.service.impl;

import com.schoolmanagement.schoolbackend.model.SessionClassSetup;
import com.schoolmanagement.schoolbackend.payload.request.SessionClassSetupDTO;
import com.schoolmanagement.schoolbackend.repository.AcademicSessionRepository;
import com.schoolmanagement.schoolbackend.repository.SectionRepository;
import com.schoolmanagement.schoolbackend.repository.SessionClassSetupRepository;
import com.schoolmanagement.schoolbackend.repository.StandardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MasterSetupService {

    @Autowired
    private SessionClassSetupRepository setupRepository;
    @Autowired
    private AcademicSessionRepository sessionRepository;
    @Autowired
    private StandardRepository standardRepository;
    @Autowired
    private SectionRepository sectionRepository;

    @Transactional
    public String saveClassSectionMapping(SessionClassSetupDTO dto) {
        // Purani mappings delete karni hai ya check karni hai, wo logic yahan laga sakte hain
        // Abhi ke liye hum seedha loop lagakar multiple sections map kar rahe hain

        for (Long sectionId : dto.getSectionIds()) {
            // Check if already mapped to prevent duplicate entries
            // Try/Catch lagaya hai in case UniqueConstraint hit ho
            try {
                SessionClassSetup setup = new SessionClassSetup();
                setup.setAcademicSession(sessionRepository.findById(dto.getSessionId()).orElseThrow());
                setup.setStandard(standardRepository.findById(dto.getStandardId()).orElseThrow());
                setup.setSection(sectionRepository.findById(sectionId).orElseThrow());
                
                if (dto.getMaxCapacity() != null) {
                    setup.setMaxCapacity(dto.getMaxCapacity());
                }

                setupRepository.save(setup);
            } catch (Exception e) {
                System.out.println("Mapping already exists for Section ID: " + sectionId);
            }
        }
        return "Mapping saved successfully!";
    }

    public List<SessionClassSetup> getSetupBySession(Long sessionId) {
        return setupRepository.findByAcademicSessionId(sessionId);
    }
}