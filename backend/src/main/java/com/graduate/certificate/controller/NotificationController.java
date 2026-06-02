package com.graduate.certificate.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.graduate.certificate.common.result.Result;
import com.graduate.certificate.entity.Notification;
import com.graduate.certificate.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 消息通知控制器
 */
@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 获取当前用户通知列表
     */
    @GetMapping
    public Result<IPage<Notification>> list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(notificationService.getUserNotifications(current, size));
    }

    /**
     * 获取未读通知数量
     */
    @GetMapping("/unread-count")
    public Result<Long> getUnreadCount() {
        return Result.success(notificationService.getUnreadCount());
    }

    /**
     * 标记为已读
     */
    @PutMapping("/{pkNotification}/read")
    public Result<Void> markAsRead(@PathVariable String pkNotification) {
        notificationService.markAsRead(pkNotification);
        return Result.success();
    }

    /**
     * 全部标记为已读
     */
    @PutMapping("/read-all")
    public Result<Void> markAllAsRead() {
        notificationService.markAllAsRead();
        return Result.success();
    }
}
