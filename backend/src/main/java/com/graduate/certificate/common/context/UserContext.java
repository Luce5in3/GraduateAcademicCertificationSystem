package com.graduate.certificate.common.context;

import lombok.Data;

/**
 * 登录用户上下文信息
 */
@Data
public class UserContext {

    /**
     * 用户主键
     */
    private String userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户类型：1-学生 2-教师
     */
    private Integer userType;

    /**
     * 学生主键（如果是学生）
     */
    private String studentId;

    /**
     * 教师主键（如果是教师）
     */
    private String teacherId;
}
