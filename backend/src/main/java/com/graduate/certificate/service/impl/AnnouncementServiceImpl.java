package com.graduate.certificate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduate.certificate.common.context.UserContextHolder;
import com.graduate.certificate.common.exception.BusinessException;
import com.graduate.certificate.common.result.ResultCode;
import com.graduate.certificate.entity.Announcement;
import com.graduate.certificate.entity.AnnouncementRead;
import com.graduate.certificate.mapper.AnnouncementMapper;
import com.graduate.certificate.mapper.AnnouncementReadMapper;
import com.graduate.certificate.service.AnnouncementService;
import com.graduate.certificate.util.PrimaryKeyGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 系统公告服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementMapper announcementMapper;
    private final AnnouncementReadMapper announcementReadMapper;

    @Override
    public IPage<Announcement> pageAnnouncements(int current, int size, Integer publishStatus) {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        if (publishStatus != null) {
            wrapper.eq(Announcement::getPublishStatus, publishStatus);
        }
        wrapper.orderByDesc(Announcement::getIsPinned)
               .orderByDesc(Announcement::getPriority)
               .orderByDesc(Announcement::getCreateTime);
        return announcementMapper.selectPage(new Page<>(current, size), wrapper);
    }

    @Override
    public Announcement getById(String pkAnnouncement) {
        return announcementMapper.selectById(pkAnnouncement);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(Announcement announcement) {
        announcement.setPkAnnouncement(PrimaryKeyGenerator.generateAnnouncementKey());
        announcement.setCreateBy(UserContextHolder.getUserId());
        announcement.setPublishStatus(0);
        announcementMapper.insert(announcement);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Announcement announcement) {
        announcementMapper.updateById(announcement);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String pkAnnouncement) {
        announcementMapper.deleteById(pkAnnouncement);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publish(String pkAnnouncement) {
        Announcement announcement = announcementMapper.selectById(pkAnnouncement);
        if (announcement == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "公告不存在");
        }
        announcement.setPublishStatus(1);
        announcement.setPublishTime(LocalDateTime.now());
        announcementMapper.updateById(announcement);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void revoke(String pkAnnouncement) {
        Announcement announcement = announcementMapper.selectById(pkAnnouncement);
        if (announcement == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "公告不存在");
        }
        announcement.setPublishStatus(2);
        announcementMapper.updateById(announcement);
    }

    @Override
    public IPage<Announcement> getPublishedForUser(int current, int size) {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Announcement::getPublishStatus, 1);

        // 根据用户角色过滤
        Integer userType = UserContextHolder.getUserType();
        if (userType != null) {
            wrapper.and(w -> w.eq(Announcement::getTargetRole, "ALL")
                    .or().eq(Announcement::getTargetRole, userType == 1 ? "STUDENT" : "TEACHER"));
        }

        wrapper.orderByDesc(Announcement::getIsPinned)
               .orderByDesc(Announcement::getPriority)
               .orderByDesc(Announcement::getPublishTime);
        return announcementMapper.selectPage(new Page<>(current, size), wrapper);
    }

    @Override
    public long getUnreadAnnouncementCount() {
        String userId = UserContextHolder.getUserId();
        // 获取所有已发布且面向当前角色的公告总数
        LambdaQueryWrapper<Announcement> announcementWrapper = new LambdaQueryWrapper<>();
        announcementWrapper.eq(Announcement::getPublishStatus, 1);
        Integer userType = UserContextHolder.getUserType();
        if (userType != null) {
            announcementWrapper.and(w -> w.eq(Announcement::getTargetRole, "ALL")
                    .or().eq(Announcement::getTargetRole, userType == 1 ? "STUDENT" : "TEACHER"));
        }
        long totalPublished = announcementMapper.selectCount(announcementWrapper);

        // 获取已读数量
        LambdaQueryWrapper<AnnouncementRead> readWrapper = new LambdaQueryWrapper<>();
        readWrapper.eq(AnnouncementRead::getPkUser, userId)
                    .eq(AnnouncementRead::getIsRead, 1);
        long totalRead = announcementReadMapper.selectCount(readWrapper);

        return Math.max(0, totalPublished - totalRead);
    }
}
