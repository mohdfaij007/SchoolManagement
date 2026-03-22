package com.schoolmanagement.schoolbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.schoolmanagement.schoolbackend.model.FeeHead;
@Repository
public interface FeeHeadRepository extends JpaRepository<FeeHead, Long> {

	// Determine if a fee name already exists to prevent duplicates
    boolean existsByHeadName(String headName);
}
