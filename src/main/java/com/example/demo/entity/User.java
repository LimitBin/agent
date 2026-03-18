package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户身份模型（你的专属名牌）
 */
@Data
@TableName("blade_user")
public class User {

    @TableId
    private Long userId;
    private String account;
    private String tenantId; // 核心：多租户ID
    private String roleName;
}