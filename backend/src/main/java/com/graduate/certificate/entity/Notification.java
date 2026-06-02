package com.graduate.certificate.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息通知实体类
 */
@Data
@TableName("notification")
public class Notification implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "pk_notification", type = IdType.INPUT)
    private String pkNotification;

    /**
     * 接收用户ID
     */
    private String pkUser;

    /**
     * 通知标题
     */
    private String title;

    /**
     * 通知内容
     */
    private String content;

    /**
     * 通知类型：1-审批结果 2-系统通知 3-申请状态变更
     */
    private Integer notificationType;

    /**
     * 关联业务ID
     */
    private String relatedId;

    /**
     * 是否已读：0-未读 1-已读
     */
    private Integer isRead;

    /**
     * 阅读时间
     */
    private LocalDateTime readTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
