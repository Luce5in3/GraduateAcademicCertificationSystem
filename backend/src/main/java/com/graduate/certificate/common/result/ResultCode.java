package com.graduate.certificate.common.result;

import lombok.Getter;

/**
 * 统一返回状态码枚举
 */
@Getter
public enum ResultCode {

    // ========== 通用状态码 ==========
    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),
    
    // ========== 客户端错误 4xx ==========
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权，请登录"),
    FORBIDDEN(403, "没有权限访问"),
    NOT_FOUND(404, "请求的资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),
    CONFLICT(409, "数据冲突"),
    
    // ========== 服务端错误 5xx ==========
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务暂时不可用"),
    
    // ========== 业务错误码 1xxx ==========
    // 用户相关 10xx
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_ALREADY_EXISTS(1002, "用户已存在"),
    USER_DISABLED(1003, "账号已被禁用"),
    USERNAME_OR_PASSWORD_ERROR(1004, "用户名或密码错误"),
    PASSWORD_ERROR(1005, "密码错误"),
    OLD_PASSWORD_ERROR(1006, "原密码错误"),
    
    // 认证相关 11xx
    TOKEN_MISSING(1101, "Token缺失"),
    TOKEN_INVALID(1102, "Token无效"),
    TOKEN_EXPIRED(1103, "Token已过期"),
    PERMISSION_DENIED(1104, "权限不足"),
    
    // 学生相关 12xx
    STUDENT_NOT_FOUND(1201, "学生信息不存在"),
    STUDENT_ALREADY_EXISTS(1202, "学生信息已存在"),
    STUDENT_NO_DUPLICATE(1203, "学号已存在"),
    
    // 教师相关 13xx
    TEACHER_NOT_FOUND(1301, "教师信息不存在"),
    TEACHER_ALREADY_EXISTS(1302, "教师信息已存在"),
    TEACHER_NO_DUPLICATE(1303, "工号已存在"),
    
    // 证明申请相关 14xx
    APPLICATION_NOT_FOUND(1401, "申请不存在"),
    APPLICATION_STATUS_ERROR(1402, "申请状态异常"),
    APPLICATION_CANNOT_CANCEL(1403, "当前状态不允许撤销"),
    APPLICATION_CANNOT_MODIFY(1404, "当前状态不允许修改"),
    
    // 审批相关 15xx
    APPROVAL_NOT_FOUND(1501, "审批记录不存在"),
    APPROVAL_NO_PERMISSION(1502, "无审批权限"),
    APPROVAL_ALREADY_DONE(1503, "已审批，不可重复操作"),
    
    // 模板相关 16xx
    TEMPLATE_NOT_FOUND(1601, "证明模板不存在"),
    TEMPLATE_DISABLED(1602, "证明模板已禁用"),
    
    // 验证相关 17xx
    VALIDATE_ERROR(1701, "参数校验失败"),
    CAPTCHA_ERROR(1702, "验证码错误");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 返回消息
     */
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
