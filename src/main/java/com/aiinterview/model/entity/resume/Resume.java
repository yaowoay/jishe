package com.aiinterview.model.entity.resume;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 简历实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("resumes")
public class Resume {

    @TableId(value = "resume_id", type = IdType.AUTO)
    private Long resumeId;

    @TableField("applicant_id")
    private Long applicantId;

    @TableField("file_url")
    private String fileUrl;

    @TableField("filename")
    private String filename;

    @TableField("upload_date")
    private LocalDateTime uploadDate;

    @TableField("parsed_data")
    private String parsedData; // JSON格式的解析数据
}
