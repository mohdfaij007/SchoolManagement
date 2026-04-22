package com.schoolmanagement.schoolbackend.service;

import com.schoolmanagement.schoolbackend.dto.BulkPromotionRequestDTO;

public interface StudentPromotionService {
    String processBulkPromotion(BulkPromotionRequestDTO requestDTO);
}