package com.artificial.smart_resume_analyzer_system.Repository;

import com.artificial.smart_resume_analyzer_system.Model.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {

}