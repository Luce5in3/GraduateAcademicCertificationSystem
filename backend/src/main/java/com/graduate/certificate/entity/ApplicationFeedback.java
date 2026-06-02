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
 * 申请反馈/申诉实体类
 */
@Data
@TableName("application_feedback")
public class ApplicationFeedback implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "pk_feedback", type = IdType.INPUT)
    private String pkFeedback;

    private String pkCa;

    private String pkUser;

    /**
     * 反馈类型：1-申诉 2-咨询 3-建议
     */
    private Integer fbkType;

    private String content;

    private String attachmentUrl;

    /**
     * 处理状态：0-待处理 1-处理中 2-已回复 3-已关闭
     */
    private Integer status;

    private String handlerPkUser;

    private String replyContent;

    private LocalDateTime replyTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
