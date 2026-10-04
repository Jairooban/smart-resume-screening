package com.smartresume.controller;

import com.smartresume.dto.ApiResponse;
import com.smartresume.nlp.SkillDefinition;
import com.smartresume.nlp.SkillExtractor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/skills")
@Tag(name = "Skills Dictionary", description = "Query curated skills catalog and autocomplete")
public class SkillController {

    private final SkillExtractor skillExtractor;

    public SkillController(SkillExtractor skillExtractor) {
        this.skillExtractor = skillExtractor;
    }

    @GetMapping
    @Operation(summary = "Get complete catalog of 200+ skills with categories and synonyms")
    public ResponseEntity<ApiResponse<List<SkillDefinition>>> getAllSkills() {
        return ResponseEntity.ok(ApiResponse.ok(skillExtractor.getAllSkillDefinitions()));
    }

    @GetMapping("/suggest")
    @Operation(summary = "Suggest skills matching prefix query")
    public ResponseEntity<ApiResponse<List<String>>> suggestSkills(
            @RequestParam(value = "q", defaultValue = "") String query) {

        String lower = query.trim().toLowerCase();
        List<String> matches = skillExtractor.getAllSkillDefinitions().stream()
                .map(SkillDefinition::getName)
                .filter(name -> lower.isEmpty() || name.toLowerCase().contains(lower))
                .distinct()
                .sorted()
                .limit(20)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.ok(matches));
    }
}
