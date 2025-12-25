package com.graduate.certificate.common.constant;

/**
 * 用户类型常量
 */
public class UserTypeConstant {

    /**
     * 学生
     */
    public static final int STUDENT = 1;

    /**
     * 教师
     */
    public static final int TEACHER = 2;

    /**
     * 管理员
     */
    public static final int ADMIN = 3;

    /**
     * 获取用户类型描述
     */
    public static String getUserTypeDesc(Integer userType) {
        if (userType == null) {
            return "未知";
        }
        switch (userType) {
            case STUDENT:
                return "学生";
            case TEACHER:
                return "教师";
            case ADMIN:
                return "管理员";
            default:
                return "未知";
        }
    }
}
