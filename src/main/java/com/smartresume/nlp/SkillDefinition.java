package com.smartresume.nlp;

import java.util.List;

/**
 * Data holder for skill definitions parsed from JSON dictionary.
 */
public class SkillDefinition {
    private String name;
    private String category;
    private List<String> synonyms;

    public SkillDefinition() {
    }

    public SkillDefinition(String name, String category, List<String> synonyms) {
        this.name = name;
        this.category = category;
        this.synonyms = synonyms;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(List<String> synonyms) {
        this.synonyms = synonyms;
    }
}
