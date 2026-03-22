package com.schoolmanagement.schoolbackend.repository;

import com.schoolmanagement.schoolbackend.model.GradeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GradeMasterRepository extends JpaRepository<GradeMaster, Long> {
    // Duplicate Check: Kya same naam ka grade pehle se hai?
    boolean existsByGradeName(String gradeName);
    
    // Check overlapping ranges (Optional but recommended for strict systems)
    // Custom query can be added later if needed.
}