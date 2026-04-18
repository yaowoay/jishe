package com.aiinterview.model.entity.student;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户收藏职位
 * 对应数据库表 user_job_collection
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserJobCollection implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer collectionId;          // 收藏ID
    private Integer userId;                // 用户ID
    private Integer jobId;                 // 职位ID
    private LocalDateTime collectTime;     // 收藏时间
    private Integer isDeleted;             // 逻辑删除 0-正常 1-已取消
}
