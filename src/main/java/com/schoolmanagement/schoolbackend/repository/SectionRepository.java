package com.schoolmanagement.schoolbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.schoolmanagement.schoolbackend.model.Section;
@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {

}
