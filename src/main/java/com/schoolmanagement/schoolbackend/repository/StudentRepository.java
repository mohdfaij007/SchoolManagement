package com.schoolmanagement.schoolbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.schoolmanagement.schoolbackend.model.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {

}
