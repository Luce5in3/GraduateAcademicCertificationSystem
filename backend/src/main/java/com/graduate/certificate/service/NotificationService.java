package com.graduate.certificate.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.graduate.certificate.entity.Notification;

/**
 * 消息通知服务接口
 */
public interface NotificationService {

    /**
     * 发送通知
     */
    void sendNotification(String pkUser, String title, String content, Integer notificationType, String relatedId);

    /**
     * 获取用户通知列表
     */
    IPage<Notification> getUserNotifications(int current, int size);

    /**
     * 获取未读通知数量
     */
    long getUnreadCount();

    /**
     * 标记为已读
     */
    void markAsRead(String pkNotification);

    /**
     * 全部标记为已读
     */
    void markAllAsRead();
}
