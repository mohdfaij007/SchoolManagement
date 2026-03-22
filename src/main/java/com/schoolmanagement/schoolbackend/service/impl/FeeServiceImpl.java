package com.schoolmanagement.schoolbackend.service.impl;

import com.schoolmanagement.schoolbackend.dto.FeeHeadDTO;
import com.schoolmanagement.schoolbackend.dto.FeeStructureDTO;
import com.schoolmanagement.schoolbackend.model.AcademicSession;
import com.schoolmanagement.schoolbackend.model.FeeHead;
import com.schoolmanagement.schoolbackend.model.FeeStructure;
import com.schoolmanagement.schoolbackend.model.Standard;
import com.schoolmanagement.schoolbackend.repository.AcademicSessionRepository; // Assumption: You have this
import com.schoolmanagement.schoolbackend.repository.*;
import com.schoolmanagement.schoolbackend.repository.FeeStructureRepository;
import com.schoolmanagement.schoolbackend.service.FeeService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeeServiceImpl implements FeeService {
    private final FeeHeadRepository feeHeadRepository;
    private final FeeStructureRepository feeStructureRepository;
    // We need this to validate the session ID from the DTO
    private final AcademicSessionRepository academicSessionRepository; 
    
//    @Autowired
    private final StandardRepository standardRepository;
// // --- MANUAL CONSTRUCTOR (Fixes the error) ---
//    public FeeServiceImpl(FeeHeadRepository feeHeadRepository, 
//                          FeeStructureRepository feeStructureRepository,
//                          AcademicSessionRepository academicSessionRepository,
//                          StandardRepository standardRepository) {
//        this.feeHeadRepository = feeHeadRepository;
//        this.feeStructureRepository = feeStructureRepository;
//        this.academicSessionRepository = academicSessionRepository;
//        this.standardRepository=standardRepository;
//        }
    // --- FEE HEAD LOGIC ---

    @Override
    public FeeHeadDTO createFeeHead(FeeHeadDTO feeHeadDTO) {
        // 1. Convert DTO to Entity
        FeeHead feeHead = new FeeHead();
        feeHead.setHeadName(feeHeadDTO.getHeadName());
        feeHead.setDescription(feeHeadDTO.getDescription());
        feeHead.setFrequency(feeHeadDTO.getFrequency());

        // 2. Save
        FeeHead savedHead = feeHeadRepository.save(feeHead);

        // 3. Convert back to DTO
        feeHeadDTO.setId(savedHead.getId());
        return feeHeadDTO;
    }

    @Override
    public List<FeeHeadDTO> getAllFeeHeads() {
        return feeHeadRepository.findAll().stream().map(head -> {
            FeeHeadDTO dto = new FeeHeadDTO();
            dto.setId(head.getId());
            dto.setHeadName(head.getHeadName());
            dto.setDescription(head.getDescription());
            dto.setFrequency(head.getFrequency());
            return dto;
        }).collect(Collectors.toList());
    }

    // --- FEE STRUCTURE LOGIC ---

    @Override
    public FeeStructureDTO createFeeStructure(FeeStructureDTO dto) {
        // 1. Validate Dependencies
        FeeHead feeHead = feeHeadRepository.findById(dto.getFeeHeadId())
                .orElseThrow(() -> new RuntimeException("Fee Head not found"));

        AcademicSession session = academicSessionRepository.findById(dto.getAcademicSessionId())
                .orElseThrow(() -> new RuntimeException("Academic Session not found"));
        
     // 1. Fetch the Standard Object (Optimization)
        Standard standard = standardRepository.findById(dto.getClassId())
                .orElseThrow(() -> new RuntimeException("Class not found"));
        
        // 2. Map to Entity
        FeeStructure structure = new FeeStructure();
        
        structure.setStandard(standard);
        structure.setFeeHead(feeHead);
        structure.setAcademicSession(session);
        structure.setAmount(dto.getAmount());
        structure.setIsMandatory(dto.getIsMandatory());
        // 3. Save
        FeeStructure savedStructure = feeStructureRepository.save(structure);

        // 4. Return DTO (Update ID)
        dto.setId(savedStructure.getId());
        return dto;
    }

    @Override
    public List<FeeStructureDTO> getFeeStructuresByClassAndSession(Long classId, Long sessionId) {
        List<FeeStructure> structures = feeStructureRepository.findByStandardIdAndAcademicSessionId(classId, sessionId);
        
        return structures.stream().map(struct -> {
            FeeStructureDTO dto = new FeeStructureDTO();
            dto.setId(struct.getId());
            dto.setClassId(struct.getStandard().getId());
            dto.setFeeHeadId(struct.getFeeHead().getId()); // Extract ID from relation
            dto.setAcademicSessionId(struct.getAcademicSession().getId());
            dto.setAmount(struct.getAmount());
            
            dto.setFeeHeadName(struct.getFeeHead().getHeadName());
            dto.setFrequency(struct.getFeeHead().getFrequency()); // Assuming 'getFrequency()' returns a String or Enum.name()
            return dto;
        }).collect(Collectors.toList());
    }
    
    
    @Override
    public List<FeeStructureDTO> getFeeStructuresBySession(Long sessionId) {
        List<FeeStructure> structures = feeStructureRepository.findByAcademicSessionId(sessionId);
        
//        // Reuse your existing mapping logic here (or extract it to a helper method to avoid code duplication)
//        return structures.stream().map(struct -> {
//            FeeStructureDTO dto = new FeeStructureDTO();
//            dto.setId(struct.getId());
//            dto.setClassId(struct.getClassId());
//            dto.setAcademicSessionId(struct.getAcademicSession().getId());
//            dto.setFeeHeadId(struct.getFeeHead().getId());
//            dto.setAmount(struct.getAmount());
//            dto.setFeeHeadName(struct.getFeeHead().getHeadName());
//            dto.setFrequency(struct.getFeeHead().getFrequency());
//            
            // IMPORTANT: We need the Class Name for the table now!
            // Assuming your FeeStructure entity has a relationship to Standard/Class entity
            // If not, you might need to fetch it or rely on a stored name. 
            // ideally: dto.setClassName(struct.getStandard().getName());
            
         // --- 🚀 THE NEW LOGIC: Fetch Class Name using ID ---
            
            // We use the ID to ask the database: "What is the name of Class #5?"
//            Standard standard = standardRepository.findById(struct.getId()).orElse(null);
//            
//            if (standard != null) {
//                // User mentioned "gradeName" is the field name earlier
//                dto.setClassName(standard.getGradeName()); 
//            } else {
//                dto.setClassName("Unknown Class");
//            }
//            // ---------------------------------------------------
//            
//            return dto;
//        }).collect(Collectors.toList());
        
        
        //      --------- Optimize way    //
     // 1. Fetch the Standard Object (Optimization)
   
        return structures.stream().map(this::mapToDTO).collect(Collectors.toList());
    }
    
    
 // --- HELPER METHOD TO MAP DATA (Used by all methods) ---
    private FeeStructureDTO mapToDTO(FeeStructure struct) {
        FeeStructureDTO dto = new FeeStructureDTO();
        dto.setId(struct.getId());
        dto.setAmount(struct.getAmount());
        
        // Map Fee Head Info
        dto.setFeeHeadId(struct.getFeeHead().getId());
        dto.setFeeHeadName(struct.getFeeHead().getHeadName());
        dto.setFrequency(struct.getFeeHead().getFrequency());

        // Map Session Info
        dto.setAcademicSessionId(struct.getAcademicSession().getId());

        // --- OPTIMIZED MAPPING ---
        // We get data directly from the Standard object inside the FeeStructure
        // No extra DB call is made here!
        dto.setClassId(struct.getStandard().getId());
        dto.setClassName(struct.getStandard().getGradeName()); 
        // -------------------------

        return dto;
    }
    
 // --- ADD THESE METHODS INSIDE FeeServiceImpl ---

    @Override
    public FeeStructureDTO getFeeStructureById(Long id) {
        FeeStructure structure = feeStructureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fee Structure not found"));
        
        // Manual Map Entity -> DTO
        FeeStructureDTO dto = new FeeStructureDTO();
        dto.setId(structure.getId());
        dto.setAmount(structure.getAmount());
        dto.setClassId(structure.getStandard().getId());
        dto.setAcademicSessionId(structure.getAcademicSession().getId());
        dto.setFeeHeadId(structure.getFeeHead().getId());
        
        // Populate extra display fields
        dto.setFeeHeadName(structure.getFeeHead().getHeadName());
        dto.setFrequency(structure.getFeeHead().getFrequency());

        return dto;
    }

    @Override
    public FeeStructureDTO updateFeeStructure(Long id, FeeStructureDTO dto) {
        FeeStructure existing = feeStructureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fee Structure not found"));

        // 1. Update simple fields
        existing.setAmount(dto.getAmount());

        // 2. Update Relationships (only if they changed)
        if (!existing.getStandard().getId().equals(dto.getClassId())) {
        	Standard standard = standardRepository.findById(dto.getClassId())
                    .orElseThrow(() -> new RuntimeException("Class (Standard) not found"));
            
            existing.setStandard(standard);
        }
        
        if (!existing.getAcademicSession().getId().equals(dto.getAcademicSessionId())) {
             AcademicSession session = academicSessionRepository.findById(dto.getAcademicSessionId())
                 .orElseThrow(() -> new RuntimeException("Session not found"));
             existing.setAcademicSession(session);
        }

        if (!existing.getFeeHead().getId().equals(dto.getFeeHeadId())) {
            FeeHead head = feeHeadRepository.findById(dto.getFeeHeadId())
                .orElseThrow(() -> new RuntimeException("Fee Head not found"));
            existing.setFeeHead(head);
        }

        // 3. Save
        FeeStructure saved = feeStructureRepository.save(existing);
        
        // 4. Return DTO (Reuse the get method to save code)
        return getFeeStructureById(saved.getId());
    }
    
    
}