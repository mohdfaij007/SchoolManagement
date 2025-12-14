package com.schoolmanagement.schoolbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.schoolmanagement.schoolbackend.model.AcademicSession;
@Repository
public interface AcademicSessionRepository extends JpaRepository<AcademicSession, Long> {

}
