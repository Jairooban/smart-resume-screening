package com.smartresume.nlp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Skill Extractor that uses a curated skills dictionary (JSON) with synonyms
 * and exact word boundary pattern matching to extract technical and soft skills.
 */
@Component
public class SkillExtractor {

    private static final Logger log = LoggerFactory.getLogger(SkillExtractor.class);

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<SkillDefinition> skillDefinitions = new ArrayList<>();
    private final Map<String, Pattern> synonymPatternMap = new LinkedHashMap<>();
    private final Map<String, String> synonymToCanonicalMap = new HashMap<>();

    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource("skills_dictionary.json");
            try (InputStream is = resource.getInputStream()) {
                List<SkillDefinition> list = objectMapper.readValue(is, new TypeReference<List<SkillDefinition>>() {});
                skillDefinitions.addAll(list);
            }

            // Build compiled patterns for fast, robust extraction
            for (SkillDefinition def : skillDefinitions) {
                String canonical = def.getName();
                List<String> allForms = new ArrayList<>();
                allForms.add(canonical);
                if (def.getSynonyms() != null) {
                    allForms.addAll(def.getSynonyms());
                }

                for (String form : allForms) {
                    String cleanForm = form.trim().toLowerCase();
                    if (cleanForm.isEmpty()) continue;
                    synonymToCanonicalMap.put(cleanForm, canonical);

                    // Create word boundary regex. Handle symbols like C++, C#, .NET
                    String patternStr;
                    if (cleanForm.equals("c++")) {
                        patternStr = "(?i)(?<![a-zA-Z0-9])c\\+\\+(?![a-zA-Z0-9])";
                    } else if (cleanForm.equals("c#")) {
                        patternStr = "(?i)(?<![a-zA-Z0-9])c#(?![a-zA-Z0-9])";
                    } else if (cleanForm.equals("c")) {
                        patternStr = "(?i)(?<![a-zA-Z0-9])c(?![a-zA-Z0-9+#])";
                    } else if (cleanForm.equals(".net") || cleanForm.equals(".net core")) {
                        patternStr = "(?i)(?<![a-zA-Z0-9])\\.net(\\s+core)?(?![a-zA-Z0-9])";
                    } else if (cleanForm.equals("r")) {
                        patternStr = "(?i)(?<![a-zA-Z0-9])r(?![a-zA-Z0-9])";
                    } else {
                        String quoted = Pattern.quote(cleanForm);
                        patternStr = "(?i)(?<![a-zA-Z0-9])" + quoted + "(?![a-zA-Z0-9])";
                    }
                    synonymPatternMap.put(cleanForm, Pattern.compile(patternStr));
                }
            }
            log.info("SkillExtractor initialized successfully with {} skill definitions and {} synonym patterns.",
                    skillDefinitions.size(), synonymPatternMap.size());
        } catch (Exception e) {
            log.error("Failed to load skills_dictionary.json", e);
        }
    }

    /**
     * Extract all matching canonical skills from input text.
     */
    public Set<String> extractSkills(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptySet();
        }

        Set<String> matchedCanonicalSkills = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        String lowerText = text.toLowerCase();

        for (Map.Entry<String, Pattern> entry : synonymPatternMap.entrySet()) {
            String synonym = entry.getKey();
            Pattern pattern = entry.getValue();

            // Quick check before running full regex
            if (lowerText.contains(synonym)) {
                if (pattern.matcher(text).find()) {
                    String canonical = synonymToCanonicalMap.get(synonym);
                    if (canonical != null) {
                        matchedCanonicalSkills.add(canonical);
                    }
                }
            }
        }

        return matchedCanonicalSkills;
    }

    /**
     * Normalize a skill name or alias into its canonical form.
     */
    public String getCanonicalSkillName(String skillName) {
        if (skillName == null) return null;
        String clean = skillName.trim().toLowerCase();
        return synonymToCanonicalMap.getOrDefault(clean, skillName.trim());
    }

    public List<SkillDefinition> getAllSkillDefinitions() {
        return Collections.unmodifiableList(skillDefinitions);
    }
}
