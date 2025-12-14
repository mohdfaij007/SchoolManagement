package com.schoolmanagement.schoolbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.schoolmanagement.schoolbackend.model.Standard;
@Repository
public interface StandardRepository extends JpaRepository<Standard, Long> {

}
