package com.graduate.certificate.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户公告已读记录实体类
 */
@Data
@TableName("announcement_read")
public class AnnouncementRead implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "pk_read", type = IdType.INPUT)
    private String pkRead;

    private String pkAnnouncement;

    private String pkUser;

    /**
     * 是否已读：0-未读 1-已读
     */
    private Integer isRead;

    private LocalDateTime readTime;

    private LocalDateTime createTime;
}
