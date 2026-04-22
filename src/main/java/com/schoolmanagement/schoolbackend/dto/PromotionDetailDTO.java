package com.schoolmanagement.schoolbackend.dto;

import com.schoolmanagement.schoolbackend.enums.PromotionStatus;
import lombok.Data;

@Data
public class PromotionDetailDTO {
    private Long studentId;
    
    private PromotionStatus promotionStatus;
    
    // These will be null if the student LEFT_SCHOOL
    private Long nextClassId;
    private Long nextSectionId;
}