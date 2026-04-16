package com.aiinterview.model.entity.resume;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 简历实体类
 * 对应数据库表：resumes
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("resumes")
public class Resume {

    @TableId(value = "resume_id", type = IdType.AUTO)
    private Long resumeId;

    /**
     * 修改点：原 applicant_id 已替换为 user_id
     */
    @TableField("user_id")
    private Long userId;

    @TableField("file_url")
    private String fileUrl;

    @TableField("filename")
    private String filename;

    @TableField("file_size")
    private Integer fileSize;

    @TableField("file_type")
    private String fileType;

    @TableField("upload_date")
    private LocalDateTime uploadDate;

    @TableField("parsed_data")
    private String parsedData; // JSON格式的解析数据

    @TableField("parse_status")
    private String parseStatus;

    @TableField("parse_time")
    private LocalDateTime parseTime;

    @TableField("error_message")
    private String errorMessage;

    @TableField("retry_count")
    private Integer retryCount;

    @TableField("is_deleted")
    private Integer isDeleted;

    @TableField("source")
    private String source;
}