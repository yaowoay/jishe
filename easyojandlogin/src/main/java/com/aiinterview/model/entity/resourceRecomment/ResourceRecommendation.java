package com.aiinterview.model.entity.resourceRecomment;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 资源推荐实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("resource_recommendations")
public class ResourceRecommendation {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("interview_evaluation_id")
    private Long interviewEvaluationId;
    
    @TableField("application_id")
    private Integer applicationId;
    
    @TableField("resource_name")
    private String resourceName; // 资源名称
    
    @TableField("resource_type")
    private String resourceType; // 资源类型：book, course, article, video, practice
    
    @TableField("resource_url")
    private String resourceUrl; // 资源链接
    
    @TableField("resource_description")
    private String resourceDescription; // 资源描述
    
    @TableField("improvement_area")
    private String improvementArea; // 对应的改进领域
    
    @TableField("priority")
    private Integer priority; // 推荐优先级 1-5
    
    @TableField("is_verified")
    private Boolean isVerified; // 资源链接是否已验证可用
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
