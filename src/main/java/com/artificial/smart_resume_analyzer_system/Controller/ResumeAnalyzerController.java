package com.artificial.smart_resume_analyzer_system.Controller;

import com.artificial.smart_resume_analyzer_system.Service.ResumeAnalyzerService;
import com.artificial.smart_resume_analyzer_system.dto.ResumeRequestDto;
import com.artificial.smart_resume_analyzer_system.dto.ResumeAnalysisResponseDto;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resumes")
public class ResumeAnalyzerController {

    private final ResumeAnalyzerService resumeAnalyzerService;

    public ResumeAnalyzerController(
            ResumeAnalyzerService resumeAnalyzerService) {

        this.resumeAnalyzerService = resumeAnalyzerService;
    }

    // Analyze resume
    @PostMapping("/analyze")
    public ResumeAnalysisResponseDto analyzeResume(
            @RequestBody ResumeRequestDto dto) {

        return resumeAnalyzerService.analyzeResume(dto);
    }

    // Get all analyzed resumes
    @GetMapping
    public List<ResumeAnalysisResponseDto> getAllAnalyses() {

        return resumeAnalyzerService.getAllAnalyses();
    }

    // Get analysis by ID
    @GetMapping("/{id}")
    public ResumeAnalysisResponseDto getAnalysisById(
            @PathVariable Long id) {

        return resumeAnalyzerService.getAnalysisById(id);
    }

    // Rank candidates
    @GetMapping("/ranking")
    public List<ResumeAnalysisResponseDto> getRankedCandidates() {

        return resumeAnalyzerService.getRankedCandidates();
    }
}