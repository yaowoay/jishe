package com.aiinterview.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户收藏职位
 * 对应数据库表 user_job_favorite
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserJobFavorite implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long favoriteId;               // 收藏ID
    private Long userId;                   // 用户ID
    private Long jobId;                    // 职位ID
    private LocalDateTime createTime;      // 创建时间
}
