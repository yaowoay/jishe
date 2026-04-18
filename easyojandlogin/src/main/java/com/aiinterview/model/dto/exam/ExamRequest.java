package com.aiinterview.model.dto.exam;

import lombok.Data;

@Data
public class ExamRequest {
    private String jobPosition;
    private String skills;
    private ExperienceEnum experience;
    private Integer questionCount;
    private DifficultyLevelEnum difficultyLevel;
    private String focusArea;

    public enum ExperienceEnum {
        LESS_THAN_ONE_YEAR("不足一年"),
        ONE_TO_THREE_YEARS("1-3年"),
        THREE_TO_FIVE_YEARS("3-5年"),
        FIVE_TO_TEN_YEARS("5-10年"),
        MORE_THAN_TEN_YEARS("10年以上");

        private final String displayName;

        ExperienceEnum(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum DifficultyLevelEnum {
        JUNIOR("初级"),
        INTERMEDIATE("中级"),
        SENIOR("高级");

        private final String displayName;

        DifficultyLevelEnum(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
