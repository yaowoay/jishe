package com.coldwind.easyoj.model.dto.request;

import lombok.Data;
import javax.validation.constraints.*;

@Data
public class ExamRequest {
    @NotBlank(message = "岗位名称不能为空")
    private String jobPosition;

    @NotBlank(message = "专业技能不能为空")
    private String skills;

    @NotNull(message = "工作经验不能为空")
    private ExperienceLevel experience;

    @Min(value = 1, message = "题目数量至少1道")
    @Max(value = 20, message = "题目数量最多20道")
    private int questionCount;  //数字填写 上下可选

    private DifficultyLevel difficultyLevel; //题目难度等级
    private String focusArea;//重点考察方向/领域 可选字段

    public enum ExperienceLevel { //枚举经验等级选择
        LESS_THAN_1_YEAR("不足一年"),
        ONE_TO_THREE_YEARS("1-3年"),
        THREE_TO_FIVE_YEARS("3-5年"),
        FIVE_TO_TEN_YEARS("5-10年"),
        MORE_THAN_10_YEARS("10年以上");

        private final String displayName;

        ExperienceLevel(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum DifficultyLevel { //枚举题目难度等级
        JUNIOR("初级"),
        INTERMEDIATE("中级"),
        SENIOR("高级");

        private final String displayName;

        DifficultyLevel(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}