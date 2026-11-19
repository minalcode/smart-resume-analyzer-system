package com.artificial.smart_resume_analyzer_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumeRequestDto {

    private String candidateName;

    private String email;

    private String jobTitle;

    private String resumeText;

    private String jobDescription;

    private int candidateExperienceYears;

    private int requiredExperienceYears;
}