// InterviewerConfig.java
package com.coldwind.easyoj.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class InterviewerConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String appearance; // 形象
    private String tone;       // 语气
    private String personality; // 性格

    public Long getId() {
        return id;
    }

    public String getAppearance() {
        return appearance;
    }

    public String getTone() {
        return tone;
    }

    public String getPersonality() {
        return personality;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAppearance(String appearance) {
        this.appearance = appearance;
    }

    public void setTone(String tone) {
        this.tone = tone;
    }

    public void setPersonality(String personality) {
        this.personality = personality;
    }
}

