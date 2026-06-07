package com.schoolmanagement.schoolbackend.repository;

import com.schoolmanagement.schoolbackend.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    // Used for the Angular "Question Bank Search" sidebar
    List<Question> findByStandardIdAndSubjectId(Long standardId, Long subjectId);
}