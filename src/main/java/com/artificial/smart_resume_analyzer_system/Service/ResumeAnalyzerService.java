package com.artificial.smart_resume_analyzer_system.Service;

import com.artificial.smart_resume_analyzer_system.Model.Resume;
import com.artificial.smart_resume_analyzer_system.Repository.ResumeRepository;
import com.artificial.smart_resume_analyzer_system.dto.ResumeRequestDto;
import com.artificial.smart_resume_analyzer_system.dto.ResumeAnalysisResponseDto;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ResumeAnalyzerService {

    private final ResumeRepository resumeRepository;

    public ResumeAnalyzerService(ResumeRepository resumeRepository) {
        this.resumeRepository = resumeRepository;
    }

    private static final List<String> SKILL_DATABASE = List.of(
            "Java",
            "Spring Boot",
            "Spring",
            "Microservices",
            "REST API",
            "MySQL",
            "SQL",
            "MongoDB",
            "React",
            "JavaScript",
            "TypeScript",
            "Angular",
            "Node.js",
            "Python",
            "HTML",
            "CSS",
            "Docker",
            "Kubernetes",
            "AWS",
            "Git",
            "Redis",
            "Kafka",
            "DSA"
    );

    @Transactional
    public ResumeAnalysisResponseDto analyzeResume(ResumeRequestDto dto) {

        // 1. Validate request

        if (dto == null ||
                isBlank(dto.getCandidateName()) ||
                isBlank(dto.getEmail()) ||
                isBlank(dto.getJobTitle()) ||
                isBlank(dto.getResumeText()) ||
                isBlank(dto.getJobDescription())) {

            throw new IllegalArgumentException(
                    "All fields are required"
            );
        }

        if (dto.getCandidateExperienceYears() < 0 ||
                dto.getRequiredExperienceYears() < 0) {

            throw new IllegalArgumentException(
                    "Experience cannot be negative"
            );
        }

        // 2. Extract skills from resume and job description

        List<String> resumeSkills =
                extractSkills(dto.getResumeText());

        List<String> requiredSkills =
                extractSkills(dto.getJobDescription());

        if (requiredSkills.isEmpty()) {
            throw new IllegalArgumentException(
                    "No recognized technical skills found in job description"
            );
        }

        // 3. Find matching and missing skills

        List<String> matchedSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();

        for (String skill : requiredSkills) {

            if (resumeSkills.contains(skill)) {
                matchedSkills.add(skill);
            } else {
                missingSkills.add(skill);
            }
        }

        // 4. Calculate skill score

        double skillMatchPercentage =
                (matchedSkills.size() * 100.0) /
                        requiredSkills.size();

        double skillScore =
                (matchedSkills.size() * 70.0) /
                        requiredSkills.size();

        // 5. Calculate experience score

        double experienceScore;

        if (dto.getRequiredExperienceYears() == 0) {

            experienceScore = 30;

        } else {

            experienceScore = Math.min(
                    (dto.getCandidateExperienceYears() * 30.0) /
                            dto.getRequiredExperienceYears(),
                    30
            );
        }

        // 6. Calculate final score

        double overallScore = Math.round(
                skillScore + experienceScore
        );

        // 7. Generate recommendation

        String recommendation;

        if (overallScore >= 80) {

            recommendation = "STRONG_MATCH";

        } else if (overallScore >= 60) {

            recommendation = "CONSIDER";

        } else {

            recommendation = "NOT_RECOMMENDED";
        }

        // 8. Save analysis

        Resume resume = new Resume();

        resume.setCandidateName(dto.getCandidateName());
        resume.setEmail(dto.getEmail());
        resume.setJobTitle(dto.getJobTitle());
        resume.setResumeText(dto.getResumeText());
        resume.setJobDescription(dto.getJobDescription());

        resume.setCandidateExperienceYears(
                dto.getCandidateExperienceYears()
        );

        resume.setRequiredExperienceYears(
                dto.getRequiredExperienceYears()
        );

        resume.setMatchedSkills(String.join(", ", matchedSkills));
        resume.setMissingSkills(String.join(", ", missingSkills));

        resume.setSkillMatchPercentage(
                Math.round(skillMatchPercentage * 100.0) / 100.0
        );

        resume.setOverallScore(overallScore);
        resume.setRecommendation(recommendation);
        resume.setAnalyzedAt(LocalDateTime.now());

        Resume savedResume = resumeRepository.save(resume);

        // 9. Return analysis response

        return new ResumeAnalysisResponseDto(
                savedResume.getId(),
                savedResume.getCandidateName(),
                savedResume.getJobTitle(),
                matchedSkills,
                missingSkills,
                savedResume.getSkillMatchPercentage(),
                savedResume.getOverallScore(),
                savedResume.getRecommendation(),
                savedResume.getAnalyzedAt()
        );
    }

    // Extract recognized skills from text

    private List<String> extractSkills(String text) {

        List<String> foundSkills = new ArrayList<>();

        String normalizedText = text.toLowerCase();

        for (String skill : SKILL_DATABASE) {

            String normalizedSkill = skill.toLowerCase();

            String regex = "(?<![a-z0-9])" +
                    java.util.regex.Pattern.quote(normalizedSkill) +
                    "(?![a-z0-9])";

            if (java.util.regex.Pattern.compile(regex)
                    .matcher(normalizedText)
                    .find()) {

                foundSkills.add(skill);
            }
        }

        return foundSkills;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    // Get all analyzed resumes

    public List<ResumeAnalysisResponseDto> getAllAnalyses() {

        return resumeRepository.findAll()
                .stream()
                .map(resume -> new ResumeAnalysisResponseDto(
                        resume.getId(),
                        resume.getCandidateName(),
                        resume.getJobTitle(),
                        splitSkills(resume.getMatchedSkills()),
                        splitSkills(resume.getMissingSkills()),
                        resume.getSkillMatchPercentage(),
                        resume.getOverallScore(),
                        resume.getRecommendation(),
                        resume.getAnalyzedAt()
                ))
                .toList();
    }

    // Get single analysis by ID

    public ResumeAnalysisResponseDto getAnalysisById(Long id) {

        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Resume analysis not found"
                ));

        return new ResumeAnalysisResponseDto(
                resume.getId(),
                resume.getCandidateName(),
                resume.getJobTitle(),
                splitSkills(resume.getMatchedSkills()),
                splitSkills(resume.getMissingSkills()),
                resume.getSkillMatchPercentage(),
                resume.getOverallScore(),
                resume.getRecommendation(),
                resume.getAnalyzedAt()
        );
    }

    private List<String> splitSkills(String skills) {

        if (skills == null || skills.isBlank()) {
            return List.of();
        }

        return List.of(skills.split(", "));
    }

    // Rank all analyzed candidates

    public List<ResumeAnalysisResponseDto> getRankedCandidates() {

        return getAllAnalyses()
                .stream()
                .sorted((a, b) ->
                        Double.compare(
                                b.getOverallScore(),
                                a.getOverallScore()
                        )
                )
                .toList();
    }
}