package com.schoolmanagement.schoolbackend.repository;

import com.schoolmanagement.schoolbackend.model.SchoolConfiguration;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SchoolConfigurationRepository extends JpaRepository<SchoolConfiguration, Long> {

    // Pessimistic Write Lock: Jab ek admission save ho raha ho, row lock ho jayegi
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT sc FROM SchoolConfiguration sc WHERE sc.schoolProfile.id = :schoolId")
    Optional<SchoolConfiguration> findBySchoolProfileIdForUpdate(@Param("schoolId") Long schoolId);
}