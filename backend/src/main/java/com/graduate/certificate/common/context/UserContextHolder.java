package com.graduate.certificate.common.context;

/**
 * 用户上下文持有者
 * 使用ThreadLocal保存当前登录用户信息
 */
public class UserContextHolder {

    private static final ThreadLocal<UserContext> contextHolder = new ThreadLocal<>();

    /**
     * 设置当前用户上下文
     */
    public static void setContext(UserContext context) {
        contextHolder.set(context);
    }

    /**
     * 获取当前用户上下文
     */
    public static UserContext getContext() {
        return contextHolder.get();
    }

    /**
     * 获取当前用户ID
     */
    public static String getUserId() {
        UserContext context = getContext();
        return context != null ? context.getUserId() : null;
    }

    /**
     * 获取当前用户名
     */
    public static String getUsername() {
        UserContext context = getContext();
        return context != null ? context.getUsername() : null;
    }

    /**
     * 获取当前用户类型
     */
    public static Integer getUserType() {
        UserContext context = getContext();
        return context != null ? context.getUserType() : null;
    }

    /**
     * 获取当前学生ID（仅学生）
     */
    public static String getStudentId() {
        UserContext context = getContext();
        return context != null ? context.getStudentId() : null;
    }

    /**
     * 获取当前教师ID（仅教师）
     */
    public static String getTeacherId() {
        UserContext context = getContext();
        return context != null ? context.getTeacherId() : null;
    }

    /**
     * 清除当前用户上下文
     */
    public static void clear() {
        contextHolder.remove();
    }
}
