package com.graduate.certificate.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 登录响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 访问令牌
     */
    private String token;

    /**
     * 用户ID（业务主键，如：U2025000001）
     */
    private String userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 用户类型：1-学生 2-教师 3-管理员
     */
    private Integer userType;

    /**
     * 用户类型描述
     */
    private String userTypeDesc;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像
     */
    private String avatar;
}
