package com.aiinterview.model.dto.workflow.resumescoring;

import lombok.Data;
import javax.validation.constraints.NotBlank;

/**
 * 简历筛选和打分请求DTO
 */
@Data
public class ResumeScoringRequest {

    /**
     * 上传文件ID（必填）
     */
    @NotBlank(message = "上传文件ID不能为空")
    private String uploadFileId;

    /**
     * 职位描述/筛选要求（必填）
     */
    @NotBlank(message = "职位描述不能为空")
    private String jd;

    /**
     * 查询内容（必填）
     */
    @NotBlank(message = "查询内容不能为空")
    private String query;

    /**
     * 用户标识（可选，默认为 user-123）
     */
    private String user = "user-123";

    /**
     * 会话ID（可选，用于继续之前的对话）
     */
    private String conversationId = "";
}
