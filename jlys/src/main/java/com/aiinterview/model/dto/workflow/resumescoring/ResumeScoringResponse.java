package com.aiinterview.model.dto.workflow.resumescoring;

import lombok.Data;

/**
 * 简历筛选和打分响应DTO
 */
@Data
public class ResumeScoringResponse {

    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 会话ID
     */
    private String conversationId;

    /**
     * 分析结果/答案
     */
    private String answer;

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 创建时间
     */
    private Long createdAt;

    /**
     * 构造成功响应
     */
    public static ResumeScoringResponse success(String messageId, String conversationId, String answer, Long createdAt) {
        ResumeScoringResponse response = new ResumeScoringResponse();
        response.setSuccess(true);
        response.setMessage("简历筛选和打分成功");
        response.setMessageId(messageId);
        response.setConversationId(conversationId);
        response.setAnswer(answer);
        response.setCreatedAt(createdAt);
        return response;
    }

    /**
     * 构造失败响应
     */
    public static ResumeScoringResponse failure(String message) {
        ResumeScoringResponse response = new ResumeScoringResponse();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }
}
