package com.aiinterview.model.dto.workflow.JD;

import lombok.Data;
import java.util.List;

/**
 * 职位描述生成响应DTO
 */
@Data
public class JobDescriptionResponse {

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 工作流运行ID
     */
    private String workflowRunId;

    /**
     * 工作流数据
     */
    private WorkflowData data;

    @Data
    public static class WorkflowData {
        /**
         * 工作流运行ID
         */
        private String id;

        /**
         * 工作流ID
         */
        private String workflowId;

        /**
         * 运行状态
         */
        private String status;

        /**
         * 输出结果
         */
        private JobOutputs outputs;

        /**
         * 错误信息
         */
        private String error;

        /**
         * 执行时间（秒）
         */
        private Double elapsedTime;

        /**
         * 总token数
         */
        private Integer totalTokens;

        /**
         * 总步骤数
         */
        private Integer totalSteps;

        /**
         * 创建时间戳
         */
        private Long createdAt;

        /**
         * 完成时间戳
         */
        private Long finishedAt;
    }

    @Data
    public static class JobOutputs {
        /**
         * 职位技能要求
         */
        private List<String> jobSkill;

        /**
         * 职位职责
         */
        private List<String> jobDuty;

        /**
         * 职位要求
         */
        private List<String> jobRequirement;
    }
}
