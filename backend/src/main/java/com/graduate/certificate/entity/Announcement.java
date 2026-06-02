package com.graduate.certificate.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统公告实体类
 */
@Data
@TableName("announcement")
public class Announcement implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "pk_announcement", type = IdType.INPUT)
    private String pkAnnouncement;

    private String title;

    private String content;

    /**
     * 目标角色：ALL/STUDENT/TEACHER
     */
    private String targetRole;

    /**
     * 优先级：0-普通 1-重要 2-紧急
     */
    private Integer priority;

    /**
     * 是否置顶：0-否 1-是
     */
    private Integer isPinned;

    /**
     * 发布状态：0-草稿 1-已发布 2-已撤回
     */
    private Integer publishStatus;

    private LocalDateTime publishTime;

    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
