package com.graduate.certificate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduate.certificate.common.context.UserContextHolder;
import com.graduate.certificate.entity.Notification;
import com.graduate.certificate.mapper.NotificationMapper;
import com.graduate.certificate.service.NotificationService;
import com.graduate.certificate.util.PrimaryKeyGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 消息通知服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendNotification(String pkUser, String title, String content, Integer notificationType, String relatedId) {
        Notification notification = new Notification();
        notification.setPkNotification(PrimaryKeyGenerator.generateNotificationKey());
        notification.setPkUser(pkUser);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setNotificationType(notificationType);
        notification.setRelatedId(relatedId);
        notification.setIsRead(0);
        notification.setCreateTime(LocalDateTime.now());
        notificationMapper.insert(notification);
        log.info("发送通知成功: pkUser={}, type={}, title={}", pkUser, notificationType, title);
    }

    @Override
    public IPage<Notification> getUserNotifications(int current, int size) {
        String userId = UserContextHolder.getUserId();
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getPkUser, userId)
               .orderByDesc(Notification::getCreateTime);
        return notificationMapper.selectPage(new Page<>(current, size), wrapper);
    }

    @Override
    public long getUnreadCount() {
        String userId = UserContextHolder.getUserId();
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getPkUser, userId)
               .eq(Notification::getIsRead, 0);
        return notificationMapper.selectCount(wrapper);
    }

    @Override
    public void markAsRead(String pkNotification) {
        Notification notification = notificationMapper.selectById(pkNotification);
        if (notification != null && notification.getIsRead() == 0) {
            notification.setIsRead(1);
            notification.setReadTime(LocalDateTime.now());
            notificationMapper.updateById(notification);
        }
    }

    @Override
    public void markAllAsRead() {
        String userId = UserContextHolder.getUserId();
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getPkUser, userId)
               .eq(Notification::getIsRead, 0);
        Notification update = new Notification();
        update.setIsRead(1);
        update.setReadTime(LocalDateTime.now());
        // 逐条更新
        notificationMapper.selectList(wrapper).forEach(n -> {
            n.setIsRead(1);
            n.setReadTime(LocalDateTime.now());
            notificationMapper.updateById(n);
        });
    }
}
