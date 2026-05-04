package com.aiinterview.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户基本信息
 * 对应数据库表 user
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;                   // 用户ID
    private String username;               // 用户名
    private String email;                  // 邮箱
    private String phone;                  // 电话
    private String password;               // 密码
    private String realName;               // 真实姓名
    private Integer gender;                // 性别 0-未知 1-男 2-女
    private String avatar;                 // 头像URL
    private String city;                   // 城市
    private String province;               // 省份
    private Integer status;                // 状态 0-禁用 1-启用
    private LocalDateTime createTime;      // 创建时间
    private LocalDateTime updateTime;      // 更新时间
    private LocalDateTime lastLoginTime;   // 最后登录时间
}
