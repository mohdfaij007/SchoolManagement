package com.schoolmanagement.schoolbackend.service;

import com.schoolmanagement.schoolbackend.dto.FeeHeadDTO;
import com.schoolmanagement.schoolbackend.dto.FeeStructureDTO;
import java.util.List;

public interface FeeService {
    
    // Fee Head Management
    FeeHeadDTO createFeeHead(FeeHeadDTO feeHeadDTO);
    List<FeeHeadDTO> getAllFeeHeads();

    // Fee Structure Management (Assigning fees to classes)
    FeeStructureDTO createFeeStructure(FeeStructureDTO feeStructureDTO);
    List<FeeStructureDTO> getFeeStructuresByClassAndSession(Long classId, Long sessionId);
    List<FeeStructureDTO> getFeeStructuresBySession(Long sessionId);
    
    
 // Add inside FeeService interface
    FeeStructureDTO getFeeStructureById(Long id);
    FeeStructureDTO updateFeeStructure(Long id, FeeStructureDTO feeStructureDTO);
  
}