package com.artificial.smart_resume_analyzer_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumeAnalysisResponseDto {

    private Long id;

    private String candidateName;

    private String jobTitle;

    private List<String> matchedSkills;

    private List<String> missingSkills;

    private double skillMatchPercentage;

    private double overallScore;

    private String recommendation;

    private LocalDateTime analyzedAt;
}