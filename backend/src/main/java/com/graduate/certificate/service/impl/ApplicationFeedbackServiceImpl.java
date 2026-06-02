package com.graduate.certificate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduate.certificate.common.context.UserContextHolder;
import com.graduate.certificate.common.exception.BusinessException;
import com.graduate.certificate.common.result.ResultCode;
import com.graduate.certificate.entity.ApplicationFeedback;
import com.graduate.certificate.mapper.ApplicationFeedbackMapper;
import com.graduate.certificate.service.ApplicationFeedbackService;
import com.graduate.certificate.util.PrimaryKeyGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 申请反馈服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationFeedbackServiceImpl implements ApplicationFeedbackService {

    private final ApplicationFeedbackMapper feedbackMapper;

    @Override
    public IPage<ApplicationFeedback> getMyFeedbacks(int current, int size) {
        String userId = UserContextHolder.getUserId();
        LambdaQueryWrapper<ApplicationFeedback> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApplicationFeedback::getPkUser, userId)
               .orderByDesc(ApplicationFeedback::getCreateTime);
        return feedbackMapper.selectPage(new Page<>(current, size), wrapper);
    }

    @Override
    public IPage<ApplicationFeedback> getByApplication(String pkCa, int current, int size) {
        LambdaQueryWrapper<ApplicationFeedback> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApplicationFeedback::getPkCa, pkCa)
               .orderByDesc(ApplicationFeedback::getCreateTime);
        return feedbackMapper.selectPage(new Page<>(current, size), wrapper);
    }

    @Override
    public ApplicationFeedback getById(String pkFeedback) {
        return feedbackMapper.selectById(pkFeedback);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(ApplicationFeedback feedback) {
        feedback.setPkFeedback(PrimaryKeyGenerator.generateApplicationFeedbackKey());
        feedback.setPkUser(UserContextHolder.getUserId());
        feedback.setStatus(0);
        feedbackMapper.insert(feedback);
        log.info("创建反馈成功: pkFeedback={}", feedback.getPkFeedback());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reply(String pkFeedback, String replyContent) {
        ApplicationFeedback feedback = feedbackMapper.selectById(pkFeedback);
        if (feedback == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "反馈不存在");
        }
        feedback.setStatus(2);
        feedback.setHandlerPkUser(UserContextHolder.getUserId());
        feedback.setReplyContent(replyContent);
        feedback.setReplyTime(LocalDateTime.now());
        feedbackMapper.updateById(feedback);
        log.info("回复反馈成功: pkFeedback={}", pkFeedback);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void close(String pkFeedback) {
        ApplicationFeedback feedback = feedbackMapper.selectById(pkFeedback);
        if (feedback == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "反馈不存在");
        }
        feedback.setStatus(3);
        feedbackMapper.updateById(feedback);
    }

    @Override
    public IPage<ApplicationFeedback> getPendingFeedbacks(int current, int size) {
        LambdaQueryWrapper<ApplicationFeedback> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApplicationFeedback::getStatus, 0)
               .orderByAsc(ApplicationFeedback::getCreateTime);
        return feedbackMapper.selectPage(new Page<>(current, size), wrapper);
    }
}
