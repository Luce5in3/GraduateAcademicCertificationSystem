package com.graduate.certificate.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.graduate.certificate.common.result.Result;
import com.graduate.certificate.entity.Announcement;
import com.graduate.certificate.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 系统公告控制器
 */
@RestController
@RequestMapping("/announcement")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    /**
     * 管理员：分页查询公告
     */
    @GetMapping("/admin")
    public Result<IPage<Announcement>> adminList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer publishStatus) {
        return Result.success(announcementService.pageAnnouncements(current, size, publishStatus));
    }

    /**
     * 管理员：获取公告详情
     */
    @GetMapping("/admin/{pkAnnouncement}")
    public Result<Announcement> adminDetail(@PathVariable String pkAnnouncement) {
        return Result.success(announcementService.getById(pkAnnouncement));
    }

    /**
     * 管理员：创建公告
     */
    @PostMapping("/admin")
    public Result<Void> create(@RequestBody Announcement announcement) {
        announcementService.create(announcement);
        return Result.success("创建成功");
    }

    /**
     * 管理员：更新公告
     */
    @PutMapping("/admin")
    public Result<Void> update(@RequestBody Announcement announcement) {
        announcementService.update(announcement);
        return Result.success("更新成功");
    }

    /**
     * 管理员：删除公告
     */
    @DeleteMapping("/admin/{pkAnnouncement}")
    public Result<Void> delete(@PathVariable String pkAnnouncement) {
        announcementService.delete(pkAnnouncement);
        return Result.success("删除成功");
    }

    /**
     * 管理员：发布公告
     */
    @PutMapping("/admin/{pkAnnouncement}/publish")
    public Result<Void> publish(@PathVariable String pkAnnouncement) {
        announcementService.publish(pkAnnouncement);
        return Result.success("发布成功");
    }

    /**
     * 管理员：撤回公告
     */
    @PutMapping("/admin/{pkAnnouncement}/revoke")
    public Result<Void> revoke(@PathVariable String pkAnnouncement) {
        announcementService.revoke(pkAnnouncement);
        return Result.success("已撤回");
    }

    /**
     * 用户端：获取已发布的公告列表
     */
    @GetMapping
    public Result<IPage<Announcement>> userList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(announcementService.getPublishedForUser(current, size));
    }

    /**
     * 用户端：获取未读公告数量
     */
    @GetMapping("/unread-count")
    public Result<Long> getUnreadCount() {
        return Result.success(announcementService.getUnreadAnnouncementCount());
    }
}
