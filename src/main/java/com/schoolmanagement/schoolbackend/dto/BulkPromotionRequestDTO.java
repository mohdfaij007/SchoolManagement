package com.schoolmanagement.schoolbackend.dto;

import lombok.Data;
import java.util.List;

@Data
public class BulkPromotionRequestDTO {
    
    private Long currentSessionId;
    private Long nextSessionId;
    
    // The list of students being processed in this batch
    private List<PromotionDetailDTO> students;
}