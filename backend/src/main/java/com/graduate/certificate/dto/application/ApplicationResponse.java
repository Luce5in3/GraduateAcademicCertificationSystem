package com.graduate.certificate.dto.application;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 申请响应DTO
 */
@Data
public class ApplicationResponse {
    
    /**
     * 申请主键
     */
    private String pkCa;
    
    /**
     * 申请编号
     */
    private String applicationNo;
    
    /**
     * 学生ID
     */
    private String pkStudent;
    
    /**
     * 证明模板ID
     */
    private String pkCt;
    
    /**
     * 证明类型
     */
    private String certificateType;
    
    /**
     * 申请理由
     */
    private String applicationReason;
    
    /**
     * 申请数据
     */
    private String applicationData;
    
    /**
     * 申请状态
     */
    private Integer status;
    
    /**
     * 状态描述
     */
    private String statusDesc;
    
    /**
     * 当前审批级别
     */
    private Integer currentApprovalLevel;
    
    /**
     * 当前审批人ID
     */
    private String pkTeacher;
    
    /**
     * 证明文件URL
     */
    private String certificateFileUrl;
    
    /**
     * 证明编号
     */
    private String certificateNo;
    
    /**
     * 开具日期
     */
    private LocalDateTime issueDate;
    
    /**
     * 申请份数
     */
    private Integer copies;
    
    /**
     * 是否加急
     */
    private Integer urgent;
    
    /**
     * 拒绝原因
     */
    private String rejectReason;
    
    /**
     * 完成时间
     */
    private LocalDateTime completeTime;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    // ========== 扩展字段（证明详情使用） ==========
    
    /**
     * 学生姓名
     */
    private String studentName;
    
    /**
     * 学号
     */
    private String studentNo;
    
    /**
     * 学院
     */
    private String college;
    
    /**
     * 专业
     */
    private String major;
    
    /**
     * 班级
     */
    private String className;
    
    /**
     * 年级
     */
    private String grade;
    
    /**
     * 学历层次
     */
    private String educationLevel;
    
    /**
     * 学习形式
     */
    private String studyType;
    
    /**
     * 入学日期
     */
    private String enrollmentDate;
    
    /**
     * 毕业日期
     */
    private String graduationDate;
    
    /**
     * 模板名称
     */
    private String templateName;
    
    /**
     * 模板编码
     */
    private String templateCode;
    
    /**
     * 模板类型
     */
    private String templateType;
    
    /**
     * 模板内容
     */
    private String templateContent;
}
