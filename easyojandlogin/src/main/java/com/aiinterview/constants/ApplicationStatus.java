package com.aiinterview.constants;

/**
 * 申请状态常量类
 * 统一管理所有申请状态值
 */
public class ApplicationStatus {
    
    /**
     * 待处理 - 刚投递的申请
     */
    public static final String PENDING = "pending";
    
    /**
     * 已查看 - 企业已查看申请
     */
    public static final String REVIEWED = "reviewed";
    
    /**
     * 面试中 - 企业已发送面试邀请
     */
    public static final String INTERVIEW = "interview";
    
    /**
     * 已接受面试 - 应聘者已接受面试邀请
     */
    public static final String INTERVIEW_ACCEPTED = "accepted";
    
    /**
     * 已录用 - 企业决定录用
     */
    public static final String HIRED = "hired";

    /**
     * 已拒绝 - 企业拒绝申请
     */
    public static final String REJECTED = "rejected";

    /**
     * 面试完成 - 面试已完成，等待结果
     */
    public static final String COMPLETED = "completed";
    
    /**
     * 获取状态的中文显示名称
     */
    public static String getDisplayName(String status) {
        if (status == null) return "未知";

        switch (status) {
            case PENDING:
                return "待处理";
            case REVIEWED:
                return "已查看";
            case INTERVIEW:
                return "面试邀请";
            case INTERVIEW_ACCEPTED:
                return "已接受面试";
            case HIRED:
                return "已录用";
            case REJECTED:
                return "已拒绝";
            case COMPLETED:
                return "面试完成";
            default:
                return status;
        }
    }
    
    /**
     * 检查状态是否有效
     */
    public static boolean isValidStatus(String status) {
        return PENDING.equals(status) ||
               REVIEWED.equals(status) ||
               INTERVIEW.equals(status) ||
               INTERVIEW_ACCEPTED.equals(status) ||
               HIRED.equals(status) ||
               REJECTED.equals(status) ||
               COMPLETED.equals(status);
    }

    /**
     * 检查状态转换是否有效
     */
    public static boolean isValidStatusTransition(String fromStatus, String toStatus) {
        if (fromStatus == null || toStatus == null) return false;

        switch (fromStatus) {
            case PENDING:
                return REVIEWED.equals(toStatus) || REJECTED.equals(toStatus);
            case REVIEWED:
                return INTERVIEW.equals(toStatus) || REJECTED.equals(toStatus);
            case INTERVIEW:
                return INTERVIEW_ACCEPTED.equals(toStatus) || REJECTED.equals(toStatus);
            case INTERVIEW_ACCEPTED:
                return COMPLETED.equals(toStatus) || REJECTED.equals(toStatus);
            case COMPLETED:
                return HIRED.equals(toStatus) || REJECTED.equals(toStatus);
            default:
                return false;
        }
    }
}
