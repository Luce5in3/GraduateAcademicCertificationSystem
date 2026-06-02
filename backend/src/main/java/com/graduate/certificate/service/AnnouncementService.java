package com.graduate.certificate.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.graduate.certificate.entity.Announcement;

/**
 * 系统公告服务接口
 */
public interface AnnouncementService {

    IPage<Announcement> pageAnnouncements(int current, int size, Integer publishStatus);

    Announcement getById(String pkAnnouncement);

    void create(Announcement announcement);

    void update(Announcement announcement);

    void delete(String pkAnnouncement);

    void publish(String pkAnnouncement);

    void revoke(String pkAnnouncement);

    /**
     * 获取用户可见的已发布公告
     */
    IPage<Announcement> getPublishedForUser(int current, int size);

    /**
     * 获取未读公告数量
     */
    long getUnreadAnnouncementCount();
}
