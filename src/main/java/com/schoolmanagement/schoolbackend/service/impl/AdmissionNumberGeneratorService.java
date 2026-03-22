package com.schoolmanagement.schoolbackend.service.impl;

import com.schoolmanagement.schoolbackend.model.SchoolConfiguration;
import com.schoolmanagement.schoolbackend.repository.SchoolConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

@Service
public class AdmissionNumberGeneratorService {

    @Autowired
    private SchoolConfigurationRepository configRepository;

    @Transactional
    public String generateNextAdmissionNumber(Long schoolId) {
        // 1. Lock ke sath Configuration fetch karein
        SchoolConfiguration config = configRepository.findBySchoolProfileIdForUpdate(schoolId)
                .orElseThrow(() -> new RuntimeException("Configuration not found for school ID: " + schoolId));

        // 2. Sequence ko +1 karein
        int nextSeq = config.getCurrentSequence() + 1;
        config.setCurrentSequence(nextSeq);

        // 3. Padding add karein (e.g., 106 ko 0106 banayein)
        String paddedSeq = String.format("%0" + config.getSequencePadding() + "d", nextSeq);

        // 4. Pattern mein replace karein
        String currentYear = String.valueOf(Year.now().getValue());
        String admissionNumber = config.getAdmissionPattern()
                .replace("[YYYY]", currentYear)
                .replace("[SEQ]", paddedSeq);

        // 5. Naya sequence DB mein save karein
        configRepository.save(config);

        return admissionNumber;
    }
}