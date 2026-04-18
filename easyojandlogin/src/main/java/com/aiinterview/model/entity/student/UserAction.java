package com.aiinterview.model.entity.student;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户行为记录
 * 对应数据库表 user_action
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAction implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long actionId;                 // 行为ID
    private Long userId;                   // 用户ID
    private String actionType;             // 行为类型：view/collect/apply/search等
    private String actionTarget;           // 行为目标：职位ID或其他
    private LocalDateTime actionTime;      // 行为时间
}
