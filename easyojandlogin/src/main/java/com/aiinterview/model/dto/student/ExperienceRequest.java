package com.aiinterview.model.dto.student;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 学生经历请求DTO
 */
@Data
public class ExperienceRequest {

    /**
     * 科研经历
     */
    private List<ResearchExperience> researchExperiences;

    /**
     * 荣誉获奖
     */
    private List<HonorAward> honorsAwards;

    /**
     * 实习经历
     */
    private List<InternshipExperience> internshipExperiences;

    @Data
    public static class ResearchExperience {
        @NotBlank(message = "项目名称不能为空")
        @Size(max = 200, message = "项目名称长度不能超过200个字符")
        private String projectName;

        @Size(max = 100, message = "角色长度不能超过100个字符")
        private String role;

        @Size(max = 100, message = "指导老师长度不能超过100个字符")
        private String supervisor;

        private String startDate;
        private String endDate;

        @Size(max = 1000, message = "项目描述长度不能超过1000个字符")
        private String description;

        @Size(max = 500, message = "成果描述长度不能超过500个字符")
        private String achievements;
    }

    @Data
    public static class HonorAward {
        @NotBlank(message = "奖项名称不能为空")
        @Size(max = 200, message = "奖项名称长度不能超过200个字符")
        private String awardName;

        @Size(max = 50, message = "奖项级别长度不能超过50个字符")
        private String level;

        private String awardDate;

        @Size(max = 200, message = "颁发机构长度不能超过200个字符")
        private String organization;

        @Size(max = 500, message = "奖项描述长度不能超过500个字符")
        private String description;
    }

    @Data
    public static class InternshipExperience {
        @NotBlank(message = "公司名称不能为空")
        @Size(max = 200, message = "公司名称长度不能超过200个字符")
        private String companyName;

        @Size(max = 100, message = "职位名称长度不能超过100个字符")
        private String position;

        @Size(max = 100, message = "部门名称长度不能超过100个字符")
        private String department;

        private String startDate;
        private String endDate;

        @Size(max = 1000, message = "工作内容长度不能超过1000个字符")
        private String workContent;

        @Size(max = 500, message = "工作成果长度不能超过500个字符")
        private String achievements;

        private List<String> skills;
    }
}