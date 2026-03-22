package com.schoolmanagement.schoolbackend.repository;

import com.schoolmanagement.schoolbackend.model.SchoolProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SchoolProfileRepository extends JpaRepository<SchoolProfile, Long> {
    Optional<SchoolProfile> findFirstByIsActiveTrue();
}